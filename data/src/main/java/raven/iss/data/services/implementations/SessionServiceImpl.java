package raven.iss.data.services.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.api.dtos.AuthorDTO;
import raven.iss.data.api.dtos.SessionDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.api.mappers.SessionMapper;
import raven.iss.data.api.mappers.UserMapper;
import raven.iss.data.constants.Messages;
import raven.iss.data.exceptions.InternalErrorException;
import raven.iss.data.exceptions.NotFoundException;
import raven.iss.data.mail.MailSender;
import raven.iss.data.model.*;
import raven.iss.data.repositories.*;
import raven.iss.data.repositories.authorFragments.AuthorRepo;
import raven.iss.data.repositories.conferenceFragments.ConferenceRepo;
import raven.iss.data.repositories.listenerFragments.ListenerRepo;
import raven.iss.data.repositories.sessionFragments.SessionRepo;
import raven.iss.data.services.interfaces.SessionService;
import raven.iss.data.validators.Helper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionMapper sessionMapper;
    private final UserMapper userMapper;

    private final SessionRepo sessionRepo;
    private final ConferenceRepo conferenceRepo;
    private final ChairRepo chairRepo;
    private final AuthorRepo authorRepo;
    private final ListenerRepo listenerRepo;
    private final PhaseRepo phaseRepo;
    private final MailSender mailSender;
    private static final String sessionRegistration = "SESSION_REGISTRATION";

    private Chair findChairFromConf(Conference conference, String username) {
        return conference.getChairs().stream()
                .filter(chair -> chair.getUser().getUsername().equals(username))
                .findFirst().orElse(null);
    }

    private Room findRoomFromConf(Conference conference, String roomName) {
        return conference.getRooms().stream()
                .filter(room -> room.getName().equals(roomName))
                .findFirst().orElse(null);
    }

    private Session findSessionFromConf(Conference conference, String sessionName) {
        return conference.getSessions().stream()
                .filter(session -> session.getName().equals(sessionName))
                .findFirst().orElse(null);
    }

    @Transactional
    @Override
    public SessionDTO addSession(Integer id, SessionDTO sessionDTO) {
        log.trace("Session service - add - cid {}, data {}", id, sessionDTO.toString());
        Session session = sessionMapper.DTOtoSession(sessionDTO);

        Conference conf = (Conference) Helper.checkNull(conferenceRepo.findByIdWithSCR(id), Messages.conferenceNotFound);
        log.trace("Session service - add - check if session already exists");
        Helper.checkNotNull(findSessionFromConf(conf, session.getName()), Messages.sessionAtConfAlreadyExists);

        log.trace("Session service - add - check if chair exists");
        Chair chair = (Chair) Helper.checkNull(findChairFromConf(conf, sessionDTO.getChair().getUser().getUsername()),
                Messages.chairNotFound);
        log.trace("Session service - add - check if room exists");
        Room room = (Room) Helper.checkNull(findRoomFromConf(conf, sessionDTO.getRoom().getName()),
                Messages.roomNotFound);

        session.setConf(conf);
        session.setChair(chair);
        chair.getSessions().add(session);
        session.setRoom(room);
        conf.getSessions().add(session);
        log.trace("Session service - add - saving data...");
        conf = conferenceRepo.save(conf);
        chairRepo.save(chair);

        log.trace("Session service - add - sending mail to {}", chair.getUser().getMail());
        mailSender.sendSessionChairNotification(chair.getUser().getMail(), conf.getName(), session.getName());

        log.trace("Session service - add - getting saved session");
        return sessionMapper.sessionToDTO(findSessionFromConf(conf, session.getName()));
    }

    @Transactional(readOnly = true)
    @Override
    public SessionDTO getSession(Integer id) {
        log.trace("Session service - get one - sid {}", id);
        return sessionMapper.sessionToDTO((Session) Helper.checkNull(sessionRepo.findById(id).orElse(null),
                Messages.sessionNotFound));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void updateSession(Integer id, SessionDTO sessionDTO) {
        log.trace("Session service - update - sid {}, data {}", id, sessionDTO.toString());
        Session session = (Session) Helper.checkNull(sessionRepo.findById(id).orElse(null), Messages.sessionNotFound);
        Session toUpdate = sessionMapper.DTOtoSession(sessionDTO);

        session.setName(toUpdate.getName() != null ? toUpdate.getName() : session.getName());
        session.setTopic(toUpdate.getTopic() != null ? toUpdate.getTopic() : session.getTopic());

        log.trace("Session service - update - saving data...");
        sessionRepo.save(session);
    }

    @Transactional
    @Override
    public void deleteSession(Integer cid, Integer id) {
        log.trace("Session service - delete - cid {}, sid {}", cid, id);
        Session session = (Session) Helper.checkNull(sessionRepo.findByIdSpeakersWatchers(id), Messages.sessionNotFound);
        log.trace("Session service - delete - fetching conference");
        Conference conference = (Conference) Helper.checkNull(conferenceRepo.findByIdWithSessions(cid), Messages.conferenceNotFound);

        log.trace("Session service - delete - removing session from speakers");
        session.getSpeakers().forEach(speaker -> {
                speaker.getSessionSpeakers().remove(session);
                authorRepo.save(speaker);
            });

        log.trace("Session service - delete - removing session from watchers");
        session.getWatchers().forEach(watcher -> {
                watcher.getAttendingSections().remove(session);
                listenerRepo.save(watcher);
            });

        log.trace("Session service - delete - saving data...");
        conference.getSessions().remove(session);
        conferenceRepo.save(conference);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SessionDTO> getAll(Integer confId) {
        log.trace("Session service - get all - cid {}", confId);
        return sessionRepo.findSessionsByConfId(confId).stream()
                .map(sessionMapper::sessionToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void attendSession(Integer cid, Integer id, String username) {
        log.trace("Session service - attend session - cid {}, sid {}, username {}", cid, id, username);
        Listener listener = listenerRepo.findByUsernameAndConfIdWithSessions(username, cid);
        log.trace("Session service - attend session - fetch session");
        Session session = (Session) Helper.checkNull(sessionRepo.findByIdWatchers(id), Messages.sessionNotFound);

        Boolean canRegister = phaseRepo.isPhaseActiveAndBeforeDeadline(cid, sessionRegistration, LocalDateTime.now());
        log.trace("Session service - attend session - check phase availability {}", canRegister);
        Helper.checkBoolean(canRegister, Messages.sessionRegistrationNotPossible);

        log.trace("Session service - attend session - check room capacity");
        Helper.checkNumbers(session.getWatchers().size(), session.getRoom().getCapacity(), Messages.notEnoughRoom);

        log.trace("Session service - attend session - save session");
        session.getWatchers().add(listener);
        session = sessionRepo.save(session);

        log.trace("Session service - attend session - save listener");
        listener.getAttendingSections().add(session);
        listenerRepo.save(listener);

        String confName = listener.getConf().getName();
        Helper.checkNull(confName, Messages.noNameConference);

        log.trace("Session service - attend session - send mail to {}", listener.getUser().getMail());
        mailSender.sendAttendSessionNotification(listener.getUser().getMail(), confName, session.getName());
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @Override
    public List<UserDTO> getAllSpeakers(Integer cid, Integer sid) {
        log.trace("Session service - get all speakers - cid {}, sid {}", cid, sid);
        Session session = (Session) Helper.checkNull(sessionRepo.findByIdSpeakers(sid), Messages.sessionNotFound);
        return session.getSpeakers().stream()
                .map(author -> userMapper.userToDTO(author.getUser()))
                .collect(Collectors.toList());
    }

    private void checkIfBothSpeakerAndChair(Session session, String username) {
        if (session.getChair().getUser().getUsername().equals(username))
            throw new InternalErrorException(Messages.bothSpeakerAndChair);
    }

    private void checkIfAlreadySpeaker(Session session, String username) {
        if (session.getSpeakers().stream()
                .map(speaker -> speaker.getUser().getUsername())
                .collect(Collectors.toList())
                .contains(username))
            throw new InternalErrorException(Messages.alreadySpeaker);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void addSpeakerToSession(Integer cid, Integer sid, AuthorDTO authorDTO) {
        log.trace("Session service - add speaker to session - cid {}, sid {}, data {}",
                cid, sid, authorDTO.toString());
        Session session = (Session) Helper.checkNull(sessionRepo.findByIdSpeakers(sid), Messages.sessionNotFound);
        log.trace("Session service - add speaker to session - check if author already speaker");
        checkIfAlreadySpeaker(session, authorDTO.getUser().getUsername());
        log.trace("Session service - add speaker to session - check if user both speaker and chair");
        checkIfBothSpeakerAndChair(session, authorDTO.getUser().getUsername());

        log.trace("Session service - add speaker to session - fetch author");
        Author author = (Author) Helper.checkNull(authorRepo.findByUsernameAndConfIdWithSessions(authorDTO.getUser().getUsername(), cid),
                Messages.authorNotFound);
        session.getSpeakers().add(author);
        author.getSessionSpeakers().add(session);
        log.trace("Session service - add speaker to session - saving data...");
        sessionRepo.save(session);
        authorRepo.save(author);

        log.trace("Session service - add speaker to session - send mail to {}", author.getUser().getMail());
        mailSender.sendSessionSpeakerNotification(author.getUser().getMail(), session.getConf().getName(), session.getName());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void deleteSpeakerFromSession(Integer cid, Integer sid, String username) {
        log.trace("Session service - delete speaker from session - cid {}, sid {}, username {}",
                cid, sid, username);
        Session session = (Session) Helper.checkNull(sessionRepo.findByIdSpeakers(sid), Messages.sessionNotFound);

        try {
            log.trace("Session service - delete speaker from session - check if user is speaker");
            checkIfAlreadySpeaker(session, username);

            log.trace("Session service - delete speaker from session - throw error");
            throw new NotFoundException(Messages.noSuchSpeakerAtSession);
        } catch (InternalErrorException ie) {
            log.trace("Session service - delete speaker from session - fetch author");
            Author author = (Author) Helper.checkNull(authorRepo.findByUsernameAndConfIdWithSessions(username, cid), Messages.authorNotFound);
            session.getSpeakers().remove(author);
            author.getSessionSpeakers().remove(session);

            log.trace("Session service - delete speaker from session - saving data...");
            sessionRepo.save(session);
            authorRepo.save(author);

            log.trace("Session service - delete speaker from session - sending mail");
            mailSender.sendSessionSpeakerRemovedNotification(username, session.getConf().getName(), session.getName());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isListenerAttending(Integer cid, Integer sid, String username) {
        log.trace("Session service - check if listener already attending session w/ cid {}, sid {}", cid, sid);
        Listener listener = listenerRepo.findByUsernameAndConfIdWithSessions(username, cid);
        log.trace("Session service - check if listener already attending session - fetch session");
        Session session = (Session) Helper.checkNull(sessionRepo.findById(sid).orElse(null), Messages.sessionNotFound);
        log.trace("Session service - check if listener already attending session - returning result");
        return listener.getAttendingSections().contains(session);
    }


}
