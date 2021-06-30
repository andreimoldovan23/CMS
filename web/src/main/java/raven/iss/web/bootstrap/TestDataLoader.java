package raven.iss.web.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import raven.iss.data.model.*;
import raven.iss.data.model.security.Authority;
import raven.iss.data.model.security.Profile;
import raven.iss.data.repositories.conferenceFragments.ConferenceRepo;
import raven.iss.data.repositories.UserRepo;
import raven.iss.data.repositories.security.AuthorityRepo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@org.springframework.context.annotation.Profile("dev")
@RequiredArgsConstructor
@SuppressWarnings("DuplicatedCode")
public class TestDataLoader implements CommandLineRunner {

    private final UserRepo userRepo;
    private final AuthorityRepo authorityRepo;
    private final PasswordEncoder passwordEncoder;
    private final ConferenceRepo conferenceRepo;

    @Override
    public void run(String... args) {
        String userPassword = passwordEncoder.encode("Greg_2#?");
        String adminPassword = passwordEncoder.encode("root_Admin_2#");

        Authority userAuthority = authorityRepo.save(Authority.builder().permission("USER").build());
        Authority adminAuthority = authorityRepo.save(Authority.builder().permission("ADMIN").build());
        Authority chairAuthority = authorityRepo.save(Authority.builder().permission("CHAIR").build());
        Authority pcMemberAuthority = authorityRepo.save(Authority.builder().permission("PCMEMBER").build());
        Authority authorAuthority = authorityRepo.save(Authority.builder().permission("AUTHOR").build());
        Authority listenerAuthority = authorityRepo.save(Authority.builder().permission("LISTENER").build());

        User admin = User.builder()
                .username("Root")
                .name("admin")
                .mail("cms_admin@gmail.com")
                .job("Administrator")
                .build();
        Profile adminProfile = Profile.builder()
                .username("Root")
                .password(adminPassword)
                .authority(adminAuthority)
                .authority(userAuthority)
                .user(admin)
                .build();
        admin.setProfile(adminProfile);
        userRepo.save(admin);

        User user = User.builder()
                .username("Greg")
                .name("Greg")
                .mail("greg@gmail.com")
                .job("Janitor")
                .build();
        Profile firstUser = Profile.builder()
                .username("Greg")
                .password(userPassword)
                .authority(userAuthority)
                .authority(chairAuthority)
                .authority(authorAuthority)
                .authority(listenerAuthority)
                .user(user)
                .build();
        user.setProfile(firstUser);
        user = userRepo.save(user);

        User mikeUser = User.builder()
                .username("Mike")
                .name("Mike")
                .mail("mike@gmail.com")
                .job("Janitor")
                .build();
        Profile mikeProfile = Profile.builder()
                .username("Mike")
                .password(userPassword)
                .authority(userAuthority)
                .authority(authorAuthority)
                .user(mikeUser)
                .build();
        mikeUser.setProfile(mikeProfile);
        mikeUser = userRepo.save(mikeUser);

        User amyUser = User.builder()
                .username("Amy")
                .name("Amy")
                .mail("amy@gmail.com")
                .job("influencer")
                .build();
        Profile amyProfile = Profile.builder()
                .username("Amy")
                .password(userPassword)
                .authority(userAuthority)
                .authority(pcMemberAuthority)
                .user(amyUser)
                .build();
        amyUser.setProfile(amyProfile);
        amyUser = userRepo.save(amyUser);

        Conference quantumConf = Conference.builder()
                .name("QuantumMechanics")
                .city("NewYork")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build();

        Phase registrationQuantum = Phase.builder()
                .name("REGISTRATION")
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .conf(quantumConf)
                .isActive(true)
                .build();

        quantumConf.getPhases().add(registrationQuantum);

        conferenceRepo.save(quantumConf);

        // ----------------------------------------
        Conference allStar = Conference.builder()
                .name("AllStar Conference")
                .city("Cluj")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build();

        Room room1 = Room.builder()
                .conf(allStar)
                .capacity(200)
                .name("Plato")
                .build();
        Room room2 = Room.builder()
                .conf(allStar)
                .capacity(100)
                .name("Finch")
                .build();
        Room room3 = Room.builder()
                .conf(allStar)
                .capacity(150)
                .name("Europa")
                .build();

        Chair gregChair = Chair.builder()
                .conf(allStar)
                .user(user)
                .build();

        Author gregAuthor = Author.builder()
                .conf(allStar)
                .user(user)
                .build();
        Author amyAuthor = Author.builder()
                .conf(allStar)
                .user(amyUser)
                .build();
        Author mikeAuthor = Author.builder()
                .conf(allStar)
                .user(mikeUser)
                .build();

        PCMember amyPCMember = PCMember.builder()
                .conf(allStar)
                .user(amyUser)
                .likeToReview(true)
                .build();

        Listener gregListener = Listener.builder()
                .conf(allStar)
                .user(user)
                .build();

        Phase phase = Phase.builder()
                .conf(allStar)
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .name("REGISTRATION")
                .build();
        Phase phase2 = Phase.builder()
                .conf(allStar)
                .deadline(LocalDateTime.of(2021, 7, 28, 12, 0, 0))
                .name("REVIEWING")
                .isActive(true)
                .build();
        Phase phase3 = Phase.builder()
                .conf(allStar)
                .deadline(LocalDateTime.of(2021, 7, 28, 12, 0, 0))
                .name("BIDDING")
                .build();

        Paper gregPaper = Paper.builder()
                .name("The Ironies of Liberation")
                .build();
        Paper mikePaper = Paper.builder()
                .name("Vlogging for dummies")
                .build();

        Review gregPaperReview = Review.builder()
                .grade(Grade.strongAccept)
                .reviewer(gregChair.getUser())
                .paper(gregPaper)
                .build();
        Review gregPaperReview2 = Review.builder()
                .grade(Grade.borderlinePaper)
                .reviewer(gregChair.getUser())
                .paper(gregPaper)
                .build();

        Session session = Session.builder()
                .conf(allStar)
                .name("How to become famous?")
                .topic("social")
                .chair(gregChair)
                .room(room1)
                .build();

        gregPaper.getAuthors().add(gregAuthor);
        gregAuthor.getPapers().add(gregPaper);

        gregPaper.getReviews().add(gregPaperReview);
        gregPaper.getReviews().add(gregPaperReview2);

        gregPaper.getShouldReview().add(amyPCMember);
        amyPCMember.getToReview().add(gregPaper);

        mikePaper.getAuthors().add(mikeAuthor);
        mikeAuthor.getPapers().add(mikePaper);

        session.getSpeakers().add(mikeAuthor);
        gregChair.getSessions().add(session);

        allStar.getSessions().add(session);
        allStar.getRooms().addAll(new HashSet<>(Set.of(room1, room2, room3)));
        allStar.getPhases().addAll(new HashSet<>(Set.of(phase, phase2, phase3)));
        allStar.getChairs().add(gregChair);
        allStar.getAuthors().addAll(new HashSet<>(Set.of(gregAuthor, mikeAuthor, amyAuthor)));
        allStar.getPcMembers().add(amyPCMember);
        allStar.getListeners().add(gregListener);
        allStar = conferenceRepo.save(allStar);

        //----------------
        Conference retroConf = Conference.builder()
                .city("LosAngeles")
                .name("Retro")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build();

        Phase inactivePhase = Phase.builder()
                .name("REGISTRATION")
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .conf(retroConf)
                .build();

        retroConf.getPhases().add(inactivePhase);

        conferenceRepo.save(retroConf);

        //-------------------
        Conference iceCreamConf = Conference.builder()
                .city("Helsinki")
                .name("IceCream")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build();

        Phase registerIceCream = Phase.builder()
                .name("REGISTRATION")
                .deadline(LocalDateTime.of(2021, 2, 10, 12, 0, 0))
                .conf(iceCreamConf)
                .isActive(true)
                .build();

        Phase sessionRegisterIceCream = Phase.builder()
                .name("SESSION_REGISTRATION")
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .conf(iceCreamConf)
                .isActive(true)
                .build();

        Chair gregIceCreamChair = Chair.builder()
                .conf(iceCreamConf)
                .user(user)
                .build();

        Author gregIceCreamAuthor = Author.builder()
                .conf(iceCreamConf)
                .user(user)
                .build();

        Listener gregIceCreamListener = Listener.builder()
                .conf(iceCreamConf)
                .user(user)
                .build();

        Room iceCreamRoom = Room.builder()
                .capacity(1)
                .name("IceCreamRoom")
                .conf(iceCreamConf)
                .build();

        Session iceCreamSession = Session.builder()
                .conf(iceCreamConf)
                .name("IceCreamSession")
                .topic("IceCreamSession")
                .room(iceCreamRoom)
                .chair(gregIceCreamChair)
                .build();
        iceCreamSession.getSpeakers().add(gregIceCreamAuthor);


        gregIceCreamAuthor.getSessionSpeakers().add(iceCreamSession);

        iceCreamConf.getPhases().add(registerIceCream);
        iceCreamConf.getPhases().add(sessionRegisterIceCream);
        iceCreamConf.getRooms().add(iceCreamRoom);
        iceCreamConf.getSessions().add(iceCreamSession);
        iceCreamConf.getChairs().add(gregIceCreamChair);
        iceCreamConf.getAuthors().add(gregIceCreamAuthor);
        iceCreamConf.getListeners().add(gregIceCreamListener);

        conferenceRepo.save(iceCreamConf);

        //-----------------------
        Phase phaseSubmitting = Phase.builder()
                .conf(allStar)
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .name("SUBMITTING")
                .isActive(true)
                .build();
        allStar.getPhases().add(phaseSubmitting);
        conferenceRepo.save(allStar);
    }
}
