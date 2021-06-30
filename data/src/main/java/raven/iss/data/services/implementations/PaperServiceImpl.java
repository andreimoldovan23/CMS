package raven.iss.data.services.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.api.dtos.AuthorDTO;
import raven.iss.data.api.dtos.PaperDTO;
import raven.iss.data.api.dtos.ReviewDTO;
import raven.iss.data.api.mappers.AuthorMapper;
import raven.iss.data.api.mappers.PaperMapper;
import raven.iss.data.api.mappers.ReviewMapper;
import raven.iss.data.constants.Messages;
import raven.iss.data.exceptions.InternalErrorException;
import raven.iss.data.exceptions.NotFoundException;
import raven.iss.data.mail.MailSender;
import raven.iss.data.model.*;
import raven.iss.data.repositories.ChairRepo;
import raven.iss.data.repositories.PhaseRepo;
import raven.iss.data.repositories.ReviewRepo;
import raven.iss.data.repositories.authorFragments.AuthorRepo;
import raven.iss.data.repositories.conferenceFragments.ConferenceRepo;
import raven.iss.data.repositories.paperFragments.PaperRepo;
import raven.iss.data.repositories.pcMemberFragments.PCMemberRepo;
import raven.iss.data.services.interfaces.PaperService;
import raven.iss.data.validators.Helper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaperServiceImpl implements PaperService {

    private final PaperRepo paperRepo;
    private final ReviewRepo reviewRepo;
    private final PhaseRepo phaseRepo;
    private final PCMemberRepo pcMemberRepo;
    private final ConferenceRepo conferenceRepo;
    private final AuthorRepo authorRepo;
    private final ChairRepo chairRepo;

    private final ReviewMapper reviewMapper;
    private final PaperMapper paperMapper;
    private final AuthorMapper authorMapper;

    private final MailSender sender;

    private static final String reviewingPhase = "REVIEWING";
    private static final String submittingPhase = "SUBMITTING";

    @Transactional
    @Override
    public PaperDTO add(PaperDTO paperDTO, Integer confId, String username) {
        log.trace("Paper service - add - cid {}, username {}, data {}", confId, username, paperDTO.toString());
        Boolean canReview = phaseRepo.isPhaseActiveAndBeforeDeadline(confId, submittingPhase, LocalDateTime.now());
        log.trace("Paper service - add - checking phase availability - {}", canReview);
        Helper.checkBoolean(canReview, Messages.submittingNotPossible);

        log.trace("Paper service - add - search author");
        Author author = (Author) Helper.checkNull(authorRepo.findByUsernameAndConfIdWithPapers(username, confId), Messages.authorNotFound);

        Paper paper = paperMapper.DTOtoPaper(paperDTO);
        author.getPapers().add(paper);
        paper.getAuthors().add(author);
        log.trace("Paper service - add - saving data...");
        author = authorRepo.save(author);

        return paperMapper.paperToDTO(author.getPapers().stream()
                .filter(paper1 -> paper1.getName().equals(paper.getName()))
                .findFirst().orElseThrow(() -> new InternalErrorException("Could not add paper")));
    }

    @Transactional
    @Override
    public void delete(Integer confId, Integer paperId) {
        log.trace("Paper service - delete - cid {}, pid {}", confId, paperId);
        Paper paper = (Paper) Helper.checkNull(paperRepo.findByIdWithAuthors(paperId), Messages.paperNotFound);

        log.trace("Paper service - delete - remove paper from authors");
        paper.getAuthors().forEach(author -> {
            author.getPapers().remove(paper);
            authorRepo.save(author);
        });

        log.trace("Paper service - delete - remove paper from reviewers");
        paper.getShouldReview().forEach(member -> {
            member.getToReview().remove(paper);
            pcMemberRepo.save(member);
        });

        log.trace("Paper service - delete - delete paper");
        paperRepo.delete(paper);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void update(PaperDTO paperDTO, Integer paperId) {
        log.trace("Paper service - update - pid {}, data {}", paperId, paperDTO.toString());
        Paper toUpdate = paperMapper.DTOtoPaper(paperDTO);

        log.trace("Paper service - update - search paper");
        Paper paper = (Paper) Helper.checkNull(paperRepo.findByIdWithTopicsAndKeywords(paperId), Messages.paperNotFound);

        paper.setName(toUpdate.getName() != null ? toUpdate.getName() : paper.getName());
        paper.setKeywords(toUpdate.getKeywords() != null ? toUpdate.getKeywords() : paper.getKeywords());
        paper.setTopics(toUpdate.getTopics() != null ? toUpdate.getTopics() : paper.getTopics());
        log.trace("Paper service - update - saving data...");
        paperRepo.save(paper);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaperDTO> findAll(Integer confId) {
        log.trace("Paper service - find all - cid {}", confId);
        return paperRepo.findAllByConfIdWithTopicsAndKeywords(confId).stream()
                .map(paperMapper::paperToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public PaperDTO findOne(Integer paperId) {
        log.trace("Paper service - find one - pid {}", paperId);
        return paperMapper.paperToDTO((Paper) Helper.checkNull(paperRepo.findByIdWithTopicsAndKeywords(paperId),
                Messages.paperNotFound));
    }

    private void checkIfAuthorOfPaper(Chair chair, String username, Integer confId, Paper paper) {
        boolean isChair = chair != null;

        // if the reviewer is chair and author and this is his paper -> review not possible
        Author author = authorRepo.findByUsernameAndConfIdWithPapers(username, confId);
        if (isChair && author != null && author.getPapers().contains(paper)) {
            throw new InternalErrorException(Messages.cannotReviewOwnPaper);
        }
    }

    private void removeReviewTaskFromPCMember(PCMember pcMember, Paper paper) {
        if (pcMember != null) {
            if (!pcMember.getToReview().contains(paper)) {
                throw new InternalErrorException(Messages.wereNotAssignedToPaper);
            } else {
                pcMember.getToReview().remove(paper);
                paper.getShouldReview().remove(pcMember);
                paperRepo.save(paper);
                pcMemberRepo.save(pcMember);
            }
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void review(String username, Integer confId, Integer paperId, ReviewDTO reviewDTO) {
        User user;
        PCMember reviewer;
        Chair chair;

        log.trace("Paper service - review - cid {}, pid {}, username {}, data {}",
                confId, paperId, username, reviewDTO.toString());
        Paper paper = (Paper) Helper.checkNull(paperRepo.findByIdWithMembersAndReviews(paperId), Messages.paperNotFound);
        log.trace("Paper service - review - check status {}", paper.getStatus().toString());
        Helper.compareStrings(false, paper.getStatus().toString(), Status.inReview.toString(), Messages.cannotReviewPaper);

        Boolean canReview = phaseRepo.isPhaseActiveAndBeforeDeadline(confId, reviewingPhase, LocalDateTime.now());
        log.trace("Paper service - review - check phase availability {}", canReview);
        Helper.checkBoolean(canReview, Messages.reviewingNotPossible);

        log.trace("Paper service - review - search user {}", username);
        try {
            log.trace("Paper service - review - if user is pc member");
            reviewer = (PCMember) Helper.checkNull(pcMemberRepo.findByUsernameAndConfIdWithPapers(username, confId),
                    Messages.userNotFound);
            Helper.checkBoolean(reviewer.getLikeToReview(), Messages.pcMemberDoesntLikeToReview);
            user = reviewer.getUser();
            chair = null;
        } catch (NotFoundException e) {
            log.trace("Paper service - review - user not pc member, might be chair");
            chair = (Chair) Helper.checkNull(chairRepo.findByUsernameAndConfId(username, confId), Messages.chairNotFound);
            user = chair.getUser();
            reviewer = null;
        }

        log.trace("Paper service - review - check if user is author of paper");
        checkIfAuthorOfPaper(chair, username, confId, paper);
        log.trace("Paper service - review - remove paper from reviewers toReview list");
        removeReviewTaskFromPCMember(reviewer, paper);

        Review newReview = Review.builder()
                .grade(reviewDTO.getGrade())
                .reviewer(user)
                .paper(paper)
                .suggestion(reviewDTO.getSuggestion())
                .build();

        paper.getReviews().add(newReview);
        log.trace("Paper service - review - saving data...");
        paperRepo.save(paper);
    }

    private void checkIfPaperAccepted(String username, Integer confId, Paper paper) {
        // an author can view his paper's reviews only if it is accepted
        Author author = authorRepo.findByUsernameAndConfIdWithPapers(username, confId);
        if (author != null && author.getPapers().contains(paper) && paper.getStatus() == Status.inReview) {
            throw new InternalErrorException("Cannot view reviews before paper is accepted");
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @Override
    public List<ReviewDTO> findAllReviews(Integer confId, Integer paperId, String username) {
        log.trace("Paper service - find all reviews - cid {}, pid {}, username {}", confId, paperId, username);
        Paper paper = (Paper) Helper.checkNull(paperRepo.findByIdWithMembersAndReviews(paperId), Messages.paperNotFound);
        log.trace("Paper service - find all reviews - check if paper was accepted");
        checkIfPaperAccepted(username, confId, paper);

        log.trace("Paper service - find all reviews - fetch data");
        return paper.getReviews().stream()
                .map(reviewMapper::reviewToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void acceptOrDecline(Integer cid, Integer pid, Status status, String username) {
        log.trace("Paper service - accept or decline - cid {}, pid {}, status {}, username {}",
                cid, pid, status.toString(), username);
        Helper.compareStrings(true, status.toString(), Status.inReview.toString(), Messages.alreadyInReview);

        Boolean canReview = phaseRepo.isPhaseActiveAndBeforeDeadline(cid, reviewingPhase, LocalDateTime.now());
        log.trace("Paper service - accept or decline - check phase availability {}", canReview);
        Helper.checkBoolean(canReview, Messages.reviewingNotPossible);

        log.trace("Paper service - accept or decline - search paper");
        Paper paper = (Paper) Helper.checkNull(paperRepo.findByIdWithAuthors(pid), Messages.paperNotFound);
        Helper.compareStrings(true, paper.getStatus().toString(), status.toString(), Messages.statusAlreadyChanged);

        log.trace("Paper service - accept or decline - check if user author of paper");
        Chair chair = chairRepo.findByUsernameAndConfId(username, cid);
        checkIfAuthorOfPaper(chair, username, cid, paper);

        paper.setStatus(status);
        log.trace("Paper service - accept or decline - saving data...");
        paperRepo.save(paper);

        log.trace("Paper service - accept or decline - sending mail");
        sendMailToAuthors(paper);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void addAuthor(Integer cid, Integer pid, AuthorDTO authorDTO, String username) {
        log.trace("Paper service - add author - cid {}, pid {}, username {}, data {}",
                cid, pid, username, authorDTO.toString());
        Author authorToAdd = (Author) Helper.checkNull(authorRepo.findByUsernameAndConfIdWithPapers(authorDTO.getUser().getUsername(), cid),
                Messages.authorNotFound);

        log.trace("Paper service - add author - search paper");
        Paper paper = (Paper) Helper.checkNull(paperRepo.findByIdWithMembersAndAuthors(pid), Messages.paperNotFound);

        paper.getAuthors().add(authorToAdd);
        authorToAdd.getPapers().add(paper);
        log.trace("Paper service - add author - saving data...");
        authorRepo.save(authorToAdd);
        paperRepo.save(paper);
    }

    @Transactional
    @Override
    public List<AuthorDTO> getAuthors(Integer cid, Integer pid, String username) {
        log.trace("Paper service - get authors - cid {}, pid {}, username {}", cid, pid, username);
        Paper paper = (Paper) Helper.checkNull(paperRepo.findByIdWithMembersAndAuthors(pid), Messages.paperNotFound);
        log.trace("Paper service - get authors - fetching data...");
        return paper.getAuthors().stream()
                .map(authorMapper::authorToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void saveFile(Integer pid, String fileLocation) {
        log.trace("Paper service - save file - pid {}, fileLocation {}", pid, fileLocation);
        Paper paper = (Paper) Helper.checkNull(paperRepo.findById(pid).orElse(null), Messages.paperNotFound);
        paper.setPaperFile(fileLocation);
        this.paperRepo.save(paper);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public byte[] getFile(Integer pid) {
        log.trace("Paper service - get file - pid {}", pid);
        Paper paper = (Paper) Helper.checkNull(paperRepo.findById(pid).orElse(null), Messages.paperNotFound);
        try {
            if (paper.getPaperFile() == null)
                return new byte[0];
            return FileUtils.readFileToByteArray(new File(paper.getPaperFile()));
        } catch (IOException ioe) {
            log.trace("Paper service - get file - error fetching file");
            throw new InternalErrorException("Couldn't fetch file");
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void savePresentationFile(Integer pid, String fileLocation) {
        log.trace("Paper service - save presentation file - pid {}, fileLocation {}", pid, fileLocation);
        Paper paper = (Paper) Helper.checkNull(paperRepo.findById(pid).orElse(null), Messages.paperNotFound);
        paper.setPresentationFile(fileLocation);
        this.paperRepo.save(paper);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public byte[] getPresentationFile(Integer pid) {
        log.trace("Paper service - get presentation file - pid {}", pid);
        Paper paper = (Paper) Helper.checkNull(paperRepo.findById(pid).orElse(null), Messages.paperNotFound);
        try {
            if (paper.getPresentationFile() == null)
                return new byte[0];
            return FileUtils.readFileToByteArray(new File(paper.getPresentationFile()));
        } catch (IOException ioe) {
            log.trace("Paper service - get presentation file - error fetching file");
            throw new InternalErrorException("Couldn't fetch presentation file");
        }
    }

    @Transactional
    @Override
    public void analysePapers(Integer cid) {
        log.trace("Paper service - analyse papers - cid {}", cid);
        Boolean canReview = phaseRepo.isPhaseActiveAndBeforeDeadline(cid, reviewingPhase, LocalDateTime.now());
        log.trace("Paper service - check phase availability {}", canReview);
        Helper.checkBoolean(canReview, Messages.reviewingNotPossible);

        StringBuilder messageToChair = new StringBuilder();

        paperRepo.findAllByConfId(cid).stream()
                .filter(paper -> paper.getStatus() == Status.inReview)
                .forEach(paper -> {
                    Status status = doesPaperPass(paper);
                    if (status != Status.inReview) {
                        paper.setStatus(status);
                        log.trace("Paper service - analyse papers - paper w/ id = {} saved with status {}",
                                paper.getId(), status.toString());
                        paperRepo.save(paper);
                        sendMailToAuthors(paper);
                    } else {
                        messageToChair.append("Paper: ").append(paper.getName())
                                .append(" requires a closer look\n");
                    }
                });

        if (messageToChair.isEmpty()) messageToChair.append("All papers have been evaluated");
        log.trace("Paper service - analyse papers - sending mail about {}", messageToChair.toString());
        sendMailToChairs(cid, messageToChair.toString());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaperDTO> findAllOfAuthor(Integer cid, String username) {
        log.trace("Paper service - find all of author - cid {}, username {}", cid, username);
        Author author = (Author) Helper.checkNull(authorRepo.findByUsernameAndConfIdWithPapers(username, cid),
                Messages.authorNotFound);
        log.trace("Paper service - find all of author - fetching data");
        return author.getPapers().stream()
                .map(paperMapper::paperToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaperDTO> findPapersToReview(Integer cid, String username) {
        log.trace("Paper service = findPapersToReview - cid {}, username {}", cid, username);
        PCMember pcMember = (PCMember) Helper.checkNull(pcMemberRepo.findByUsernameAndConfIdWithPapers(username, cid),
                Messages.pcmemberNotFound);
        log.trace("Paper service - find papers to review - fetching data");
        return pcMember.getToReview().stream()
                .map(paperMapper::paperToDTO)
                .collect(Collectors.toList());
    }

    private Status doesPaperPass(Paper paper) {
        AtomicReference<Integer> pass = new AtomicReference<>(0);
        AtomicReference<Integer> decline = new AtomicReference<>(0);
        AtomicReference<Integer> borderline = new AtomicReference<>(0);
        reviewRepo.findAllByPaperId(paper.getId()).stream()
                .map(Review::getGrade)
                .forEach(grade -> {
                    if(List.of(Grade.strongAccept, Grade.accept, Grade.weakAccept).contains(grade)) {
                        pass.updateAndGet(v -> v + 1);
                    } else if (List.of(Grade.reject, Grade.weakReject, Grade.strongReject).contains(grade)) {
                        decline.updateAndGet(v -> v + 1);
                    } else {
                        borderline.updateAndGet(v -> v + 1);
                    }
                });

        if (pass.get() > decline.get() + borderline.get() ||
                (borderline.get() >= 0 && pass.get() >= decline.get() + borderline.get())) return Status.accepted;
        else if (decline.get() > pass.get() + borderline.get() ||
                (borderline.get() >= 0 && decline.get() >= pass.get() + borderline.get())) return Status.declined;
        else return Status.inReview;
    }

    private void sendMailToAuthors(Paper paper) {
        List<String> authors = paper.getAuthors().stream()
                .map(author -> author.getUser().getMail())
                .collect(Collectors.toList());
        String confName = paper.getAuthors().stream().map(author -> author.getConf().getName()).findAny().orElse(null);

        Helper.checkNull(confName, Messages.noNameConference);

        sender.sendPaperResultNotification(authors, confName, paper.getName(),
                paper.getStatus().toString());
    }

    private void sendMailToChairs(Integer cid, String message) {
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findByIdWithChairs(cid),
                Messages.conferenceNotFound);

        conference.getChairs()
                .forEach(chair -> sender.sendPaperStatusAfterCalculation(chair.getUser().getMail(), conference.getName(),
                        message));
    }

}
