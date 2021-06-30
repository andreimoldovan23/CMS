package raven.iss.data.services.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.api.dtos.LoginDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.api.mappers.LoginMapper;
import raven.iss.data.api.mappers.UserMapper;
import raven.iss.data.constants.Messages;
import raven.iss.data.mail.MailSender;
import raven.iss.data.model.Author;
import raven.iss.data.model.Conference;
import raven.iss.data.model.Listener;
import raven.iss.data.model.User;
import raven.iss.data.model.security.Profile;
import raven.iss.data.repositories.PhaseRepo;
import raven.iss.data.repositories.UserRepo;
import raven.iss.data.repositories.conferenceFragments.ConferenceRepo;
import raven.iss.data.repositories.security.AuthorityRepo;
import raven.iss.data.services.interfaces.UserService;
import raven.iss.data.validators.Helper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("DuplicatedCode")
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final LoginMapper loginMapper;
    private final UserRepo userRepo;
    private final ConferenceRepo conferenceRepo;
    private final AuthorityRepo authorityRepo;
    private final PasswordEncoder passwordEncoder;
    private final PhaseRepo phaseRepo;
    private final MailSender sender;
    private static final String registrationPhase = "REGISTRATION";

    @Transactional(readOnly = true)
    @Override
    public UserDTO findDTObyUsername(String username) {
        log.trace("User service - find dto by username {}", username);
        return userMapper.userToDTO(userRepo.findByUsername(username));
    }

    @Override
    public User findByUsername(String username) {
        log.trace("User service - find by username {}", username);
        return userRepo.findByUsername(username);
    }

    @Transactional
    @Override
    public void signUp(LoginDTO userDTO) {
        log.trace("User service - sign up - data {}", userDTO.toString());
        Helper.checkNotNull(userRepo.findByUsername(userDTO.getUsername()), Messages.alreadyHaveAccount);

        User user = loginMapper.DTOtoUser(userDTO);
        Profile profile = Profile.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .user(user)
                .authority(authorityRepo.findByPermission("USER"))
                .build();

        log.trace("User service - sign up - saving data...");
        user.setProfile(profile);
        userRepo.save(user);

        log.trace("User service - sign up - sending mail");
        sender.sendSignUpNotification(user.getMail());
    }

    private Listener findListenerFromConf(String username, Conference conference) {
        return conference.getListeners().stream()
                .filter(listener -> listener.getUser().getUsername().equals(username))
                .findFirst().orElse(null);
    }

    private Author findAuthorFromConf(String username, Conference conference) {
        return conference.getAuthors().stream()
                .filter(author -> author.getUser().getUsername().equals(username))
                .findFirst().orElse(null);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void registerListener(String username, Integer confId) {
        log.trace("User service - register as listener - username {}, confId {}", username, confId);
        User user = (User) Helper.checkNull(userRepo.findByUsername(username), Messages.userNotFound);
        log.trace("User service - register as listener - fetching conference");
        Conference conf = (Conference) Helper.checkNull(conferenceRepo.findByIdWithListeners(confId), Messages.conferenceNotFound);

        Boolean canRegister = phaseRepo.isPhaseActiveAndBeforeDeadline(confId, registrationPhase, LocalDateTime.now());
        log.trace("User service - register as listener - check phase availability {}", canRegister);
        Helper.checkBoolean(canRegister, Messages.registrationNotPossible);

        log.trace("User service - register as listener - checking if already listener");
        Helper.checkNotNull(findListenerFromConf(username, conf), Messages.alreadyListener);

        user.getProfile().addAuthority(authorityRepo.findByPermission("LISTENER"));
        user.getProfile().setNoPass(true);
        User savedUser = userRepo.save(user);
        Listener listener = Listener.builder()
                .user(savedUser)
                .conf(conf)
                .build();
        log.trace("User service - register as listener - saving data...");
        conf.getListeners().add(listener);
        conferenceRepo.save(conf);

        log.trace("User service - register as listener - sending mail to {}", savedUser.getMail());
        sender.sendRegisterListenerNotification(savedUser.getMail(), conf.getName());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDTO> getAll() {
        return StreamSupport.stream(userRepo.findAll().spliterator(), false)
                .map(userMapper::userToDTO)
                .filter(dto -> !dto.getUsername().equals("Root"))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public void registerAuthor(String username, Integer confId) {
        log.trace("User service - register as author - username {}, confId {}", username, confId);
        User user = (User) Helper.checkNull(userRepo.findByUsername(username), Messages.userNotFound);
        log.trace("User service - register as author - fetching conference");
        Conference conf = (Conference) Helper.checkNull(conferenceRepo.findByIdWithAuthors(confId), Messages.conferenceNotFound);

        Boolean canRegister = phaseRepo.isPhaseActiveAndBeforeDeadline(confId, registrationPhase, LocalDateTime.now());
        log.trace("User service - register as author - check phase availability {}", canRegister);
        Helper.checkBoolean(canRegister, Messages.registrationNotPossible);

        log.trace("User service - register as author - checking if already author");
        Helper.checkNotNull(findAuthorFromConf(username, conf), Messages.alreadyAuthor);

        user.getProfile().addAuthority(authorityRepo.findByPermission("AUTHOR"));
        user.getProfile().setNoPass(true);
        User savedUser = userRepo.save(user);
        Author author = Author.builder()
                .user(savedUser)
                .conf(conf)
                .build();
        log.trace("User service - register as author - saving data...");
        conf.getAuthors().add(author);
        conferenceRepo.save(conf);

        log.trace("User service - register as author - sending mail to {}", savedUser.getMail());
        sender.sendRegisterAuthorNotification(savedUser.getMail(), conf.getName());
    }

}
