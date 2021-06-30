package raven.iss.data.services.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.api.dtos.*;
import raven.iss.data.api.mappers.*;
import raven.iss.data.constants.Messages;
import raven.iss.data.exceptions.InternalErrorException;
import raven.iss.data.mail.MailSender;
import raven.iss.data.model.*;
import raven.iss.data.repositories.PhaseRepo;
import raven.iss.data.repositories.UserRepo;
import raven.iss.data.repositories.authorFragments.AuthorRepo;
import raven.iss.data.repositories.conferenceFragments.ConferenceRepo;
import raven.iss.data.repositories.paperFragments.PaperRepo;
import raven.iss.data.repositories.pcMemberFragments.PCMemberRepo;
import raven.iss.data.repositories.security.AuthorityRepo;
import raven.iss.data.services.interfaces.ConferenceService;
import raven.iss.data.validators.Helper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConferenceServiceImpl implements ConferenceService {

    private static final String biddingPhase = "BIDDING";

    private final PCMemberMapper pcMemberMapper;
    private final ConferenceRepo conferenceRepo;
    private final AuthorityRepo authorityRepo;
    private final UserRepo userRepo;
    private final PCMemberRepo pcMemberRepo;
    private final AuthorRepo authorRepo;
    private final PhaseRepo phaseRepo;
    private final PaperRepo paperRepo;

    private final ConferenceMapper conferenceMapper;
    private final ChairMapper chairMapper;
    private final UserMapper userMapper;
    private final AuthorMapper authorMapper;

    private final MailSender sender;

    @Transactional(readOnly = true)
    @Override
    public List<ConferenceDTO> getAll() {
        log.trace("Conference service - find all");
        return StreamSupport.stream(conferenceRepo.findAll().spliterator(), false)
                .map(conferenceMapper::conferenceToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ConferenceDTO findDTObyId(Integer id) {
        log.trace("Conference service - find one w/ id = {}", id);
        return conferenceMapper.conferenceToDTO(
                (Conference) Helper.checkNull(conferenceRepo.findById(id).orElse(null), Messages.conferenceNotFound)
        );
    }

    @Transactional
    @Override
    public ConferenceDTO add(ConferenceDTO dto) {
        log.trace("Conference service - add w/ data = {}", dto.toString());
        Conference conference = conferenceMapper.DTOtoConference(dto);
        log.trace("Conference service - add - validating start and end dates - {}, {}", conference.getStartDate(), conference.getEndDate());
        Helper.validateDates(conference.getStartDate(), conference.getEndDate(), Messages.invalidDates);
        log.trace("Conference service - add - saving data...");
        return conferenceMapper.conferenceToDTO(conferenceRepo.save(conference));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void edit(Integer id, ConferenceDTO dto) {
        log.trace("Conference service - edit w/ id = {}, data = {}", id, dto.toString());
        Conference toUpdate = conferenceMapper.DTOtoConference(dto);

        log.trace("Conference service - edit - validating start and end dates - {}, {}", toUpdate.getStartDate(), toUpdate.getEndDate());
        Helper.validateDates(toUpdate.getStartDate(), toUpdate.getEndDate(), Messages.invalidDates);

        log.trace("Conference service - edit - searching conference");
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findById(id).orElse(null), Messages.conferenceNotFound);
        conference.setName(toUpdate.getName() != null ? toUpdate.getName() : conference.getName());
        conference.setCity(toUpdate.getCity() != null ? toUpdate.getCity() : conference.getCity());
        conference.setStartDate(toUpdate.getStartDate() != null ?
                toUpdate.getStartDate() : conference.getStartDate());
        conference.setEndDate(toUpdate.getEndDate() != null ?
                toUpdate.getEndDate() : conference.getEndDate());

        log.trace("Conference service - edit - saving data...");
        conferenceRepo.save(conference);
    }

    @Transactional
    @Override
    public void deleteById(Integer id) {
        log.trace("Conference service - delete w/ id = {}", id);
        Helper.checkNull(conferenceRepo.findById(id).orElse(null), Messages.conferenceNotFound);
        log.trace("Conference service - delete - deleting data...");
        conferenceRepo.deleteById(id);
    }

    private Chair isChairInConference(Conference conference, String username) {
        return conference.getChairs().stream()
            .filter(chair -> chair.getUser().getUsername().equals(username))
            .findFirst().orElse(null);
    }

    private void isPCMemberInConference(Conference conference, String username) {
        conference.getPcMembers().stream()
                .filter(pcMember -> pcMember.getUser().getUsername().equals(username))
                .findFirst().ifPresent(pc -> {
                    throw new InternalErrorException(Messages.alreadyPCMember);
        });
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void addChair(Integer id, ChairDTO chairDTO) {
        log.trace("Conference service - add chair - cid {}, chairData {}", id, chairDTO.toString());
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findByIdWithChairs(id), Messages.conferenceNotFound);

        log.trace("Conference service - add chair - checking if chair is already in conference");
        Helper.checkNotNull(isChairInConference(conference, chairDTO.getUser().getUsername()), Messages.alreadyChair);
        Chair chair = chairMapper.DTOtoChair(chairDTO);

        log.trace("Conference service - add chair - searching user, username {}", chair.getUser().getUsername());
        User user = (User) Helper.checkNull(userRepo.findByUsername(chair.getUser().getUsername()), Messages.userNotFound);

        user.getProfile().addAuthority(authorityRepo.findByPermission("CHAIR"));
        chair.setUser(userRepo.save(user));
        conference.getChairs().add(chair);
        chair.setConf(conference);

        log.trace("Conference service - add chair - saving data...");
        conferenceRepo.save(conference);

        log.trace("Conference service - add chair - sending notification e-mail to {}", user.getMail());
        sender.sendChairNotification(user.getMail(), conference.getName());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChairDTO> getAllChairs(Integer cid) {
        log.trace("Conference service - get all chairs - cid {}", cid);
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findByIdWithChairs(cid), Messages.conferenceNotFound);
        log.trace("Conference service - get all chairs - fetching data");
        return conference.getChairs().stream()
                .map(chairMapper::chairToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void deleteChair(Integer cid, String username) {
        log.trace("Conference service - delete chair - cid {}, username {}", cid, username);
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findByIdWithChairs(cid), Messages.conferenceNotFound);
        log.trace("Conference service - delete chair - checking if chair already in conference");
        Chair chair = (Chair) Helper.checkNull(isChairInConference(conference, username), Messages.chairNotFound);
        conference.getChairs().remove(chair);
        log.trace("Conference service - delete chair - saving data...");
        conferenceRepo.save(conference);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDTO> filterNotChair(Integer cid, String currentUsername) {
        log.trace("Conference service - filter not chairs - cid {}, currentUsername {}", cid, currentUsername);
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findByIdWithChairs(cid), Messages.conferenceNotFound);
        List<String> chairsUsernames = conference.getChairs().stream()
                .map(chair -> chair.getUser().getUsername())
                .collect(Collectors.toList());

        log.trace("Conference service - filter not chairs - chair usernames are {}", chairsUsernames);
        return StreamSupport.stream(userRepo.findAll().spliterator(), false)
                .filter(user -> !chairsUsernames.contains(user.getUsername())
                        && !user.getUsername().equals("Root") && !user.getUsername().equals(currentUsername))
                .map(userMapper::userToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDTO> filterNotPcMember(Integer cid, String currentUsername) {
        log.trace("Conference service - filter not pc members - cid {}, currentUsername {}", cid, currentUsername);
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findByIdWithPCMembers(cid), Messages.conferenceNotFound);
        List<String> pcMemberUsernames = conference.getPcMembers().stream()
                .map(member -> member.getUser().getUsername())
                .collect(Collectors.toList());

        log.trace("Conference service - filter not pc members - pc member usernames are {}", pcMemberUsernames);
        return StreamSupport.stream(userRepo.findAll().spliterator(), false)
                .filter(user -> !pcMemberUsernames.contains(user.getUsername())
                        && !user.getUsername().equals("Root") && !user.getUsername().equals(currentUsername))
                .map(userMapper::userToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
    @Override
    public List<PCMemberDTO> getPcMembersNotAssignedSpecificPaper(Integer cid, Integer pid) {
        log.trace("Conference service - filter - pcmembers for specific paper - cid {}, pid {}", cid, pid);
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findByIdWithMembersPapers(cid), Messages.conferenceNotFound);

        Paper paper = (Paper) Helper.checkNull(paperRepo.findById(pid).orElse(null), Messages.paperNotFound);
        log.trace("Conference service - filter - pcmembers for specific paper");
        return conference.getPcMembers().stream()
                .filter(pcMember -> !pcMember.getToReview().contains(paper) && pcMember.getLikeToReview())
                .map(pcMemberMapper::pcMemberToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public PCMemberDTO getPcMemberBidded(Integer cid, String username) {
        log.trace("Conference service - check if pc member bidded w/ cid {}, username {}", cid, username);
        PCMember pcMember = (PCMember) Helper.checkNull(pcMemberRepo.findByUsernameAndConfId(username, cid), Messages.pcmemberNotFound);
        log.trace("Conference service - check bidded - returning data");
        return pcMemberMapper.pcMemberToDTO(pcMember);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AuthorDTO> filterAuthorsWithAcceptedPapers(Integer cid) {
        log.trace("Conference service - get authors from conf with accepted papers w/ cid {}", cid);
        return authorRepo.findAllFromConfWithPapers(cid).stream()
                .filter(author -> author.getPapers().stream()
                        .anyMatch(paper -> paper.getStatus().equals(Status.accepted)))
                .map(authorMapper::authorToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AuthorDTO> getAllAuthors(Integer cid) {
        log.trace("Conference service - get all authors w/ cid {}", cid);
        return authorRepo.findAllByConfId(cid)
                .stream()
                .map(authorMapper::authorToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PCMemberDTO> getAllPcMembers(Integer cid) {
        log.trace("Conference service - get all pcmembers - cid {}", cid);
        return pcMemberRepo.findAllByConfId(cid).stream()
                .map(pcMemberMapper::pcMemberToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void addPcMember(Integer cid, PCMemberDTO dto, String currentUsername) {
        log.trace("Conference service - add pc member - cid {}, currentUsername {}, member data {}",
                cid, currentUsername, dto.toString());
        Helper.compareStrings(true, currentUsername, dto.getUser().getUsername(), Messages.cannotBecomePC);

        log.trace("Conference service - add pc member - search conference");
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findByIdWithPCMembers(cid), Messages.conferenceNotFound);
        log.trace("Conference service - add pc member - check if already pc member in conference");
        isPCMemberInConference(conference, dto.getUser().getUsername());

        PCMember pcMember = pcMemberMapper.DTOtoPCMember(dto);
        log.trace("Conference service - add pc member - search username w/ {}", pcMember.getUser().getUsername());
        User user = (User) Helper.checkNull(userRepo.findByUsername(pcMember.getUser().getUsername()), Messages.userNotFound);

        user.getProfile().addAuthority(authorityRepo.findByPermission("PCMEMBER"));
        pcMember.setUser(userRepo.save(user));
        conference.getPcMembers().add(pcMember);
        pcMember.setConf(conference);

        log.trace("Conference service - add pc member - saving data...");
        conferenceRepo.save(conference);

        log.trace("Conference service - send mail to {}", user.getMail());
        sender.sendPCMemberNotification(user.getMail(), conference.getName());
    }

    @Transactional
    @Override
    public void bid(Integer cid, String username, Boolean likesToReview) {
        log.trace("Conference service - bid - cid {}, username {}, likesToReview {}", cid, username, likesToReview);
        Boolean canRegister = phaseRepo.isPhaseActiveAndBeforeDeadline(cid, biddingPhase, LocalDateTime.now());
        log.trace("Conference service - bid - check phase availability - {}", canRegister);
        Helper.checkBoolean(canRegister, Messages.biddingNotPossible);

        PCMember member = pcMemberRepo.findByUsernameAndConfId(username, cid);

        log.trace("Conference service - bid - checking pc member {}", member.toString());
        Helper.checkNull(member, Messages.pcmemberNotFound);

        member.setLikeToReview(likesToReview);
        log.trace("Conference service - bid - saving data...");
        pcMemberRepo.save(member);
    }

    private void checkIfAuthorPCMember(PCMember pcMember, Paper paper) {
        if(paper.getAuthors().stream()
                .map(author -> author.getUser().getUsername())
                .collect(Collectors.toList())
                .contains(pcMember.getUser().getUsername()))
            throw new InternalErrorException(Messages.cannotAssignPaperToItsAuthor);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void addReviewerToPaper(Integer cid, Integer pid, PCMemberDTO pcMemberDTO) {
        log.trace("Conference service - add reviewer to paper - cid {}, pid {}, data {}",
                cid, pid, pcMemberDTO);
        PCMember pcMember = pcMemberRepo.findByUsernameAndConfIdWithPapers(pcMemberDTO.getUser().getUsername(), cid);
        log.trace("Conference service - add reviewer to paper - searching paper");
        Paper paper = (Paper) Helper.checkNull(paperRepo.findByIdWithMembersAndAuthors(pid), Messages.paperNotFound);

        log.trace("Conference service - add reviewer to paper - validating pc member and if he wants to review");
        Helper.checkNull(pcMember, Messages.pcmemberNotFound);
        Helper.checkBoolean(pcMember.getLikeToReview(), Messages.pcMemberDoesntLikeToReview);

        log.trace("Conference service - add reviewer to paper - check if pc member is also author of paper");
        checkIfAuthorPCMember(pcMember, paper);

        pcMember.getToReview().add(paper);
        paper.getShouldReview().add(pcMember);
        log.trace("Conference service - add reviewer to paper - saving data...");
        paperRepo.save(paper);

        log.trace("Conference service - send mail to {}", pcMember.getUser().getMail());
        String confName = pcMember.getConf().getName();
        sender.sendAssignedPaperNotification(pcMember.getUser().getMail(), confName, paper.getName());
    }


}
