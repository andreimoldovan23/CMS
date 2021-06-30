package raven.iss.web.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import raven.iss.data.model.*;
import raven.iss.data.model.security.Authority;
import raven.iss.data.model.security.Profile;
import raven.iss.data.repositories.ChairRepo;
import raven.iss.data.repositories.conferenceFragments.ConferenceRepo;
import raven.iss.data.repositories.sessionFragments.SessionRepo;
import raven.iss.data.repositories.UserRepo;
import raven.iss.data.repositories.security.AuthorityRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@org.springframework.context.annotation.Profile("prod")
@RequiredArgsConstructor
@SuppressWarnings("DuplicatedCode")
public class AdminDataLoader implements CommandLineRunner {

    private final UserRepo userRepo;
    private final AuthorityRepo authorityRepo;
    private final PasswordEncoder passwordEncoder;
    private final ConferenceRepo conferenceRepo;
    private final static String filePath = "FileStorage\\Papers";

    @Override
    public void run(String... args) throws Exception {
        // password strings
        String adminPassword = passwordEncoder.encode("root_Admin_2#");
        String userPassword = passwordEncoder.encode("Greg_2#?");

        // authority creation
        Authority userAuthority = authorityRepo.save(Authority.builder().permission("USER").build());
        Authority adminAuthority = authorityRepo.save(Authority.builder().permission("ADMIN").build());
        Authority chairAuthority = authorityRepo.save(Authority.builder().permission("CHAIR").build());
        Authority pcMemberAuthority = authorityRepo.save(Authority.builder().permission("PCMEMBER").build());
        Authority authorAuthority = authorityRepo.save(Authority.builder().permission("AUTHOR").build());
        Authority listenerAuthority = authorityRepo.save(Authority.builder().permission("LISTENER").build());

        // load admin
        User admin = User.builder()
                .username("Root")
                .name("Root")
                .mail("moldovanandrei2301@gmail.com")
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
        // --

        // load users & profiles
        User userJames = User.builder()
                .username("James")
                .name("James Martinez")
                .mail("moldovanandrei2301@gmail.com")
                .job("Aerospace Engineer")
                .build();
        Profile jamesProfile = Profile.builder()
                .username("James")
                .password(userPassword)
                .authority(userAuthority)
                .authority(chairAuthority)
                .authority(authorAuthority)
                .authority(listenerAuthority)
                .user(userJames)
                .build();
        userJames.setProfile(jamesProfile);
        userJames = userRepo.save(userJames);

        User userSusan = User.builder()
                .username("Susan")
                .name("Susan Taylor")
                .mail("moldovanandrei2301@gmail.com")
                .job("Marketing Manager")
                .build();
        Profile susanProfile = Profile.builder()
                .username("Susan")
                .password(userPassword)
                .authority(userAuthority)
                .authority(chairAuthority)
                .authority(authorAuthority)
                .user(userSusan)
                .build();
        userSusan.setProfile(susanProfile);
        userSusan = userRepo.save(userSusan);

        User userMichael = User.builder()
                .username("Michael")
                .name("Michael Lee")
                .mail("moldovanandrei2301@gmail.com")
                .job("Architect")
                .build();
        Profile michaelProfile = Profile.builder()
                .username("Michael")
                .password(userPassword)
                .authority(userAuthority)
                .authority(authorAuthority)
                .authority(chairAuthority)
                .user(userMichael)
                .build();
        userMichael.setProfile(michaelProfile);
        userMichael = userRepo.save(userMichael);

        User userElizabeth = User.builder()
                .username("Elizabeth")
                .name("Elizabeth White")
                .mail("moldovanandrei2301@gmail.com")
                .job("Software Developer")
                .build();
        Profile elizabethProfile = Profile.builder()
                .username("Elizabeth")
                .password(userPassword)
                .authority(userAuthority)
                .authority(chairAuthority)
                .user(userElizabeth)
                .build();
        userElizabeth.setProfile(elizabethProfile);
        userElizabeth = userRepo.save(userElizabeth);

        User userDavid = User.builder()
                .username("David")
                .name("David Sanchez")
                .mail("moldovanandrei2301@gmail.com")
                .job("Physician")
                .build();
        Profile davidProfile = Profile.builder()
                .username("David")
                .password(userPassword)
                .authority(userAuthority)
                .authority(pcMemberAuthority)
                .authority(chairAuthority)
                .authority(listenerAuthority)
                .user(userDavid)
                .build();
        userDavid.setProfile(davidProfile);
        userDavid = userRepo.save(userDavid);

        User userAmy = User.builder()
                .username("Amy")
                .name("Amy Walker")
                .mail("moldovanandrei2301@gmail.com")
                .job("Real Estate Agent")
                .build();
        Profile amyProfile = Profile.builder()
                .username("Amy")
                .password(userPassword)
                .authority(userAuthority)
                .authority(pcMemberAuthority)
                .authority(authorAuthority)
                .user(userAmy)
                .build();
        userAmy.setProfile(amyProfile);
        userAmy = userRepo.save(userAmy);

        User userThomas = User.builder()
                .username("Thomas")
                .name("Thomas Hill")
                .mail("moldovanandrei2301@gmail.com")
                .job("Computer Systems Analyst")
                .build();
        Profile thomasProfile = Profile.builder()
                .username("Thomas")
                .password(userPassword)
                .authority(userAuthority)
                .authority(pcMemberAuthority)
                .authority(authorAuthority)
                .user(userThomas)
                .build();
        userThomas.setProfile(thomasProfile);
        userThomas = userRepo.save(userThomas);

        User userLisa = User.builder()
                .username("Lisa")
                .name("Lisa Baker")
                .mail("moldovanandrei2301@gmail.com")
                .job("Software Developer")
                .build();
        Profile lisaProfile = Profile.builder()
                .username("Lisa")
                .password(userPassword)
                .authority(userAuthority)
                .authority(pcMemberAuthority)
                .authority(authorAuthority)
                .authority(listenerAuthority)
                .user(userLisa)
                .build();
        userLisa.setProfile(lisaProfile);
        userLisa = userRepo.save(userLisa);

        User userGaston = User.builder()
                .username("Gaston")
                .name("Gaston Tiger")
                .mail("moldovanandrei2301@gmail.com")
                .job("Zoologist")
                .build();
        Profile gastonProfile = Profile.builder()
                .username("Gaston")
                .password(userPassword)
                .authority(userAuthority)
                .authority(pcMemberAuthority)
                .authority(listenerAuthority)
                .user(userGaston)
                .build();
        userGaston.setProfile(gastonProfile);
        userGaston = userRepo.save(userGaston);

        User userAshley = User.builder()
                .username("Ashley")
                .name("Ashley Moore")
                .mail("moldovanandrei2301@gmail.com")
                .job("Radiologic Technologist")
                .build();
        Profile ashleyProfile = Profile.builder()
                .username("Ashley")
                .password(userPassword)
                .authority(userAuthority)
                .authority(pcMemberAuthority)
                .authority(listenerAuthority)
                .user(userAshley)
                .build();
        userAshley.setProfile(ashleyProfile);
        userAshley = userRepo.save(userAshley);

        User userRonald = User.builder()
                .username("Ronald")
                .name("Ronald Martin")
                .mail("moldovanandrei2301@gmail.com")
                .job("Psychologist")
                .build();
        Profile ronaldProfile = Profile.builder()
                .username("Ronald")
                .password(userPassword)
                .authority(userAuthority)
                .authority(authorAuthority)
                .authority(pcMemberAuthority)
                .authority(listenerAuthority)
                .user(userRonald)
                .build();
        userRonald.setProfile(ronaldProfile);
        userRonald = userRepo.save(userRonald);

        User userLaura = User.builder()
                .username("Laura")
                .name("Laura Clark")
                .mail("moldovanandrei2301@gmail.com")
                .job("Accountant")
                .build();
        Profile lauraProfile = Profile.builder()
                .username("Laura")
                .password(userPassword)
                .authority(userAuthority)
                .authority(authorAuthority)
                .authority(pcMemberAuthority)
                .authority(listenerAuthority)
                .user(userLaura)
                .build();
        userLaura.setProfile(lauraProfile);
        userLaura = userRepo.save(userLaura);

        User userScott = User.builder()
                .username("Scott")
                .name("Scott Young")
                .mail("moldovanandrei2301@gmail.com")
                .job("Systems Analyst")
                .build();
        Profile scottProfile = Profile.builder()
                .username("Scott")
                .password(userPassword)
                .authority(userAuthority)
                .authority(authorAuthority)
                .authority(chairAuthority)
                .authority(listenerAuthority)
                .user(userScott)
                .build();
        userScott.setProfile(scottProfile);
        userScott = userRepo.save(userScott);
        // ---

        // load conferences
        Conference topoConference = Conference.builder()
                .name("TOPO Summit")
                .city("San Francisco")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build();
            // load chairs pcmembers and authors
        Chair jamesChair = Chair.builder()
                .conf(topoConference)
                .user(userJames)
                .build();
        Chair susanChair = Chair.builder()
                .conf(topoConference)
                .user(userSusan)
                .build();
        PCMember davidPCMember = PCMember.builder()
                .conf(topoConference)
                .user(userDavid)
                .likeToReview(true)
                .build();
        PCMember amyPCMember = PCMember.builder()
                .conf(topoConference)
                .user(userAmy)
                .likeToReview(true)
                .build();
        PCMember thomasPCMember = PCMember.builder()
                .conf(topoConference)
                .user(userThomas)
                .likeToReview(true)
                .build();
        PCMember lisaPCMember = PCMember.builder()
                .conf(topoConference)
                .user(userLisa)
                .likeToReview(true)
                .build();
        Author michaelAuthor = Author.builder()
                .conf(topoConference)
                .user(userMichael)
                .build();
        Author ronaldAuthor = Author.builder()
                .conf(topoConference)
                .user(userRonald)
                .build();
        Author scottAuthor = Author.builder()
                .conf(topoConference)
                .user(userScott)
                .build();
        Author lauraAuthor = Author.builder()
                .conf(topoConference)
                .user(userLaura)
                .build();
        Author lisaAuthor = Author.builder()
                .conf(topoConference)
                .user(userLisa)
                .build();
        Listener gastonListener = Listener.builder()
                .conf(topoConference)
                .user(userGaston)
                .build();
        Listener ashleyListener = Listener.builder()
                .conf(topoConference)
                .user(userAshley)
                .build();
        Listener ronaldListener = Listener.builder()
                .conf(topoConference)
                .user(userRonald)
                .build();
        Listener lauraListener = Listener.builder()
                .conf(topoConference)
                .user(userLaura)
                .build();
        Listener davidListener = Listener.builder()
                .conf(topoConference)
                .user(userDavid)
                .build();
            // ---
            // load papers
        Paper michaelPaper = Paper.builder()
                .name("Craft a Plan on a Page")
                .paperFile(filePath + "\\TOPO\\Craft a Plan on a Page.pdf")
                .presentationFile(filePath + "\\TOPO\\Craft a Plan on a Page.pdf")
                .topics(new HashSet<>(Set.of("Ongoing performance conversations", "Performance improvement", "Goal setting")))
                .keywords(new HashSet<>(Set.of("management", "budged", "sales", "B2B", "B2C")))
                .build();
        michaelPaper.getAuthors().add(michaelAuthor);
        michaelAuthor.getPapers().add(michaelPaper);
        Paper ronaldPaper = Paper.builder()
                .name("Find true north")
                .paperFile(filePath + "\\TOPO\\Find true north.pdf")
                .presentationFile(filePath + "\\TOPO\\Find true north.pdf")
                .topics(new HashSet<>(Set.of("Ongoing performance conversations", "Performance improvement", "Goal setting")))
                .keywords(new HashSet<>(Set.of("management", "budged", "sales", "B2B", "B2C")))
                .build();
        ronaldPaper.getAuthors().add(ronaldAuthor);
        ronaldAuthor.getPapers().add(ronaldPaper);
        ronaldPaper.getAuthors().add(lisaAuthor);
        lisaAuthor.getPapers().add(ronaldPaper);
        Paper scottPaper = Paper.builder()
                .name("Plan the Work and Work the Plan")
                .paperFile(filePath + "\\TOPO\\Plan the Work and Work the Plan.pdf")
                .presentationFile(filePath + "\\TOPO\\Plan the Work and Work the Plan.pdf")
                .topics(new HashSet<>(Set.of("Ongoing performance conversations", "Performance improvement", "Goal setting")))
                .keywords(new HashSet<>(Set.of("management", "budged", "sales", "B2B", "B2C")))
                .build();
        scottPaper.getAuthors().add(scottAuthor);
        scottAuthor.getPapers().add(scottPaper);

        Paper lauraPaper = Paper.builder()
                .name("The Ironies of Liberation")
                .paperFile(filePath + "\\TOPO\\The Ironies of Liberation.pdf")
                .presentationFile(filePath + "\\TOPO\\The Ironies of Liberation.pdf")
                .topics(new HashSet<>(Set.of("Ongoing performance conversations", "Performance improvement", "Goal setting")))
                .keywords(new HashSet<>(Set.of("management", "budged", "sales", "B2B", "B2C")))
                .build();
        lauraPaper.getAuthors().add(lauraAuthor);
        lauraAuthor.getPapers().add(lauraPaper);
        // ---
            // Assign papers to reviewers
        davidPCMember.getToReview().add(michaelPaper);
        amyPCMember.getToReview().add(michaelPaper);
        lisaPCMember.getToReview().add(michaelPaper);

        davidPCMember.getToReview().add(ronaldPaper);
        amyPCMember.getToReview().add(ronaldPaper);
        thomasPCMember.getToReview().add(ronaldPaper);
        lisaPCMember.getToReview().add(ronaldPaper);

        thomasPCMember.getToReview().add(scottPaper);
        lisaPCMember.getToReview().add(scottPaper);
        davidPCMember.getToReview().add(lauraPaper);
        amyPCMember.getToReview().add(lauraPaper);

            // ---
            // load rooms
        Room room1 = Room.builder()
                .conf(topoConference)
                .capacity(200)
                .name("Europa")
                .build();
        Room room2 = Room.builder()
                .conf(topoConference)
                .capacity(200)
                .name("Finch")
                .build();
        Room room3 = Room.builder()
                .conf(topoConference)
                .capacity(200)
                .name("Plato")
                .build();
        Room room4 = Room.builder()
                .conf(topoConference)
                .capacity(200)
                .name("Alpha")
                .build();
            //---
        // load phases
        Phase registrationPhaseTopo = Phase.builder()
                .conf(topoConference)
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .name("REGISTRATION")
                .isActive(true)
                .build();
        Phase session_registrationPhaseTopo = Phase.builder()
                .conf(topoConference)
                .deadline(LocalDateTime.of(2021, 7, 30, 12, 0, 0))
                .name("SESSION_REGISTRATION")
                .build();

        Phase biddingPhaseTopo = Phase.builder()
                .conf(topoConference)
                .deadline(LocalDateTime.of(2021, 7, 27, 12, 0, 0))
                .name("BIDDING")
                .build();

        Phase reviewingPhaseTopo = Phase.builder()
                .conf(topoConference)
                .deadline(LocalDateTime.of(2021, 7, 28, 12, 0, 0))
                .name("REVIEWING")
                .build();

        Phase submittingPhaseTopo = Phase.builder()
                .conf(topoConference)
                .deadline(LocalDateTime.of(2021, 7, 25, 12, 0, 0))
                .name("SUBMITTING")
                .isActive(true)
                .build();
        // ---
        // load sessions
        Session session1Topo = Session.builder()
                .conf(topoConference)
                .chair(jamesChair)
                .name("Sales Effectiveness")
                .topic("customer experience")
                .room(room1)
                .build();
        session1Topo.getSpeakers().add(michaelAuthor);
        session1Topo.getSpeakers().add(ronaldAuthor);
        session1Topo.getSpeakers().add(scottAuthor);
        michaelAuthor.getSessionSpeakers().add(session1Topo);
        ronaldAuthor.getSessionSpeakers().add(session1Topo);
        scottAuthor.getSessionSpeakers().add(session1Topo);

        Session session2Topo = Session.builder()
                .conf(topoConference)
                .chair(susanChair)
                .name("Marketing Ops and Technology")
                .topic("ideas and technology")
                .room(room2)
                .build();
        session2Topo.getSpeakers().add(lauraAuthor);
        session2Topo.getSpeakers().add(lisaAuthor);
        lauraAuthor.getSessionSpeakers().add(session1Topo);
        lisaAuthor.getSessionSpeakers().add(session1Topo);

        // ---

        topoConference.getRooms().addAll(new ArrayList<>(List.of(room1, room2, room3, room4)));
        topoConference.getSessions().add(session1Topo);
        topoConference.getSessions().add(session2Topo);
        topoConference.getPhases().addAll(new ArrayList<>(List.of(biddingPhaseTopo, registrationPhaseTopo,
                reviewingPhaseTopo, submittingPhaseTopo, session_registrationPhaseTopo)));
        topoConference.getChairs().add(jamesChair);
        topoConference.getChairs().add(susanChair);
        topoConference.getPcMembers().add(davidPCMember);
        topoConference.getPcMembers().add(amyPCMember);
        topoConference.getPcMembers().add(thomasPCMember);
        topoConference.getPcMembers().add(lisaPCMember);
        topoConference.getAuthors().add(michaelAuthor);
        topoConference.getAuthors().add(ronaldAuthor);
        topoConference.getAuthors().add(scottAuthor);
        topoConference.getAuthors().add(lauraAuthor);
        topoConference.getAuthors().add(lisaAuthor);
        topoConference.getListeners().add(gastonListener);
        topoConference.getListeners().add(ashleyListener);
        topoConference.getListeners().add(ronaldListener);
        topoConference.getListeners().add(lauraListener);
        topoConference.getListeners().add(davidListener);
        conferenceRepo.save(topoConference);
        // ---
//----------------------------------------------------------------------------------------
        Conference iotConference = Conference.builder()
                .name("Internet of Things World")
                .city("Silicon Valley")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build();
            // load charis, pcmembers and authors
        Chair michaelChair = Chair.builder()
                .conf(iotConference)
                .user(userMichael)
                .build();
        Chair elizabethChair = Chair.builder()
                .conf(iotConference)
                .user(userElizabeth)
                .build();
        Chair davidChair = Chair.builder()
                .conf(iotConference)
                .user(userDavid)
                .build();
        PCMember gastonPCMember = PCMember.builder()
                .conf(iotConference)
                .user(userGaston)
                .likeToReview(true)
                .build();
        PCMember ashleyPCMember = PCMember.builder()
                .conf(iotConference)
                .user(userAshley)
                .likeToReview(true)
                .build();
        PCMember ronaldPCMember = PCMember.builder()
                .conf(iotConference)
                .user(userRonald)
                .likeToReview(true)
                .build();
        PCMember lauraPCMember = PCMember.builder()
                .conf(iotConference)
                .user(userLaura)
                .likeToReview(true)
                .build();
        Author amyAuthor = Author.builder()
                .conf(iotConference)
                .user(userAmy)
                .build();
        Author thomasAuthor = Author.builder()
                .conf(iotConference)
                .user(userThomas)
                .build();
        Author jamesAuthor = Author.builder()
                .conf(iotConference)
                .user(userJames)
                .build();
        Author susanAuthor = Author.builder()
                .conf(iotConference)
                .user(userSusan)
                .build();
        Listener jamesListener = Listener.builder()
                .conf(topoConference)
                .user(userJames)
                .build();
        Listener scottListener = Listener.builder()
                .conf(topoConference)
                .user(userScott)
                .build();
        Listener lisaListener = Listener.builder()
                .conf(topoConference)
                .user(userLisa)
                .build();
            // ---
            // load papers
        Paper amyPaper = Paper.builder()
                .name("How to secure privacy in the IoT")
                .paperFile(filePath + "\\IoT\\How to secure privacy in the IoT.pdf")
                .presentationFile(filePath + "\\IoT\\How to secure privacy in the IoT.pdf")
                .topics(new HashSet<>(Set.of("Digitization, digitalization and disruption",
                        "Cloud computing", "AI in business")))
                .keywords(new HashSet<>(Set.of("AES", "Bid Data", "LoRa Protocol", "Radiofrequency", "Encryption")))
                .build();
        amyPaper.getAuthors().add(amyAuthor);
        amyAuthor.getPapers().add(amyPaper);
        Paper thomasPaper = Paper.builder()
                .name("IoT Architecture - are traditional architectures good enough?")
                .paperFile(filePath + "\\IoT\\IoT Architecture are traditional architectures good enough.pdf")
                .presentationFile(filePath + "\\IoT\\IoT Architecture are traditional architectures good enough.pdf")
                .topics(new HashSet<>(Set.of("Digitization, digitalization and disruption",
                        "Cloud computing", "AI in business")))
                .keywords(new HashSet<>(Set.of("AES", "Bid Data", "LoRa Protocol", "Radiofrequency", "Encryption")))
                .build();
        thomasPaper.getAuthors().add(thomasAuthor);
        thomasAuthor.getPapers().add(thomasPaper);
        Paper jamesPaper = Paper.builder()
                .name("IoT Cloud architecture")
                .paperFile(filePath + "\\IoT\\IoT Cloud architecture.pdf")
                .presentationFile(filePath + "\\IoT\\IoT Cloud architecture.pdf")
                .topics(new HashSet<>(Set.of("Digitization, digitalization and disruption",
                        "Cloud computing", "AI in business")))
                .keywords(new HashSet<>(Set.of("AES", "Bid Data", "LoRa Protocol", "Radiofrequency", "Encryption")))
                .build();
        jamesPaper.getAuthors().add(jamesAuthor);
        jamesAuthor.getPapers().add(jamesPaper);
        Paper susanPaper = Paper.builder()
                .name("Understanding the Internet of Things Protocols")
                .paperFile(filePath + "\\IoT\\Understanding the Internet of Things Protocols.pdf")
                .presentationFile(filePath + "\\IoT\\Understanding the Internet of Things Protocols.pdf")
                .topics(new HashSet<>(Set.of("Digitization, digitalization and disruption",
                        "Cloud computing", "AI in business")))
                .keywords(new HashSet<>(Set.of("AES", "Bid Data", "LoRa Protocol", "Radiofrequency", "Encryption")))
                .build();
        susanPaper.getAuthors().add(susanAuthor);
        susanAuthor.getPapers().add(susanPaper);
            // ---
            // assign papers to reviewers
        gastonPCMember.getToReview().add(amyPaper);
        ashleyPCMember.getToReview().add(amyPaper);
        lauraPCMember.getToReview().add(amyPaper);
        ronaldPCMember.getToReview().add(thomasPaper);
        lauraPCMember.getToReview().add(thomasPaper);
        gastonPCMember.getToReview().add(jamesPaper);
        ashleyPCMember.getToReview().add(jamesPaper);
        ronaldPCMember.getToReview().add(jamesPaper);
        lauraPCMember.getToReview().add(jamesPaper);
        ashleyPCMember.getToReview().add(susanPaper);
        lauraPCMember.getToReview().add(susanPaper);
            // ---
            // load rooms
        Room room5 = Room.builder()
                .conf(iotConference)
                .capacity(200)
                .name("Dakota")
                .build();
        Room room6 = Room.builder()
                .conf(iotConference)
                .capacity(200)
                .name("Steam")
                .build();
        Room room7 = Room.builder()
                .conf(iotConference)
                .capacity(200)
                .name("Venus")
                .build();
        Room room8 = Room.builder()
                .conf(iotConference)
                .capacity(200)
                .name("Ideas Hall")
                .build();
            // ---
            // load phases
        Phase registrationPhaseIot = Phase.builder()
                .conf(iotConference)
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .name("REGISTRATION")
                .isActive(true)
                .build();
        Phase session_registrationPhaseIot = Phase.builder()
                .conf(iotConference)
                .deadline(LocalDateTime.of(2021, 7, 30, 12, 0, 0))
                .name("SESSION_REGISTRATION")
                .build();

        Phase biddingPhaseIot = Phase.builder()
                .conf(iotConference)
                .deadline(LocalDateTime.of(2021, 7, 27, 12, 0, 0))
                .name("BIDDING")
                .build();

        Phase reviewingPhaseIot = Phase.builder()
                .conf(iotConference)
                .deadline(LocalDateTime.of(2021, 7, 28, 12, 0, 0))
                .name("REVIEWING")
                .build();

        Phase submittingPhaseIot = Phase.builder()
                .conf(iotConference)
                .deadline(LocalDateTime.of(2021, 7, 25, 12, 0, 0))
                .name("SUBMITTING")
                .build();
            // ---
            // load sessions
        Session session1Iot = Session.builder()
                .conf(iotConference)
                .chair(davidChair)
                .name("Future of IoT")
                .topic("smart manufacturing")
                .room(room1)
                .build();
        session1Iot.getSpeakers().add(amyAuthor);
        session1Iot.getSpeakers().add(thomasAuthor);
        amyAuthor.getSessionSpeakers().add(session1Iot);
        thomasAuthor.getSessionSpeakers().add(session1Iot);

        Session session2Iot = Session.builder()
                .conf(iotConference)
                .chair(elizabethChair)
                .name("Cloud computing")
                .topic("marketing")
                .room(room2)
                .build();
        session2Iot.getSpeakers().add(jamesAuthor);
        session2Iot.getSpeakers().add(susanAuthor);
        jamesAuthor.getSessionSpeakers().add(session2Iot);
        susanAuthor.getSessionSpeakers().add(session2Iot);
        // ---

        iotConference.getRooms().addAll(new ArrayList<>(List.of(room5, room6, room7, room8)));
        iotConference.getSessions().add(session1Iot);
        iotConference.getSessions().add(session2Iot);
        iotConference.getPhases().addAll(new ArrayList<>(List.of(biddingPhaseIot, registrationPhaseIot,
                reviewingPhaseIot, submittingPhaseIot, session_registrationPhaseIot)));
        iotConference.getChairs().add(michaelChair);
        iotConference.getChairs().add(elizabethChair);
        iotConference.getChairs().add(davidChair);
        iotConference.getPcMembers().add(gastonPCMember);
        iotConference.getPcMembers().add(ashleyPCMember);
        iotConference.getPcMembers().add(ronaldPCMember);
        iotConference.getPcMembers().add(lauraPCMember);
        iotConference.getAuthors().add(amyAuthor);
        iotConference.getAuthors().add(thomasAuthor);
        iotConference.getAuthors().add(jamesAuthor);
        iotConference.getAuthors().add(susanAuthor);
        iotConference.getListeners().add(jamesListener);
        iotConference.getListeners().add(scottListener);
        iotConference.getListeners().add(lisaListener);
        // ---

        conferenceRepo.save(iotConference);

//  -------------------------------------------------------------
        Conference quantumConf = Conference.builder()
                .name("QuantumMechanics")
                .city("NewYork")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build();

        // load phases
        Phase registrationPhaseQuant = Phase.builder()
                .conf(quantumConf)
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .name("REGISTRATION")
                .isActive(true)
                .build();
        Phase session_registrationPhaseQuant = Phase.builder()
                .conf(quantumConf)
                .deadline(LocalDateTime.of(2021, 7, 30, 12, 0, 0))
                .name("SESSION_REGISTRATION")
                .build();

        Phase biddingPhaseQuant = Phase.builder()
                .conf(quantumConf)
                .deadline(LocalDateTime.of(2021, 7, 27, 12, 0, 0))
                .name("BIDDING")
                .build();

        Phase reviewingPhaseQuant = Phase.builder()
                .conf(quantumConf)
                .deadline(LocalDateTime.of(2021, 7, 28, 12, 0, 0))
                .name("REVIEWING")
                .build();

        Phase submittingPhaseQuant = Phase.builder()
                .conf(quantumConf)
                .deadline(LocalDateTime.of(2021, 7, 25, 12, 0, 0))
                .name("SUBMITTING")
                .isActive(true)
                .build();
        // ---
        Chair scottChair = Chair.builder()
                .conf(quantumConf)
                .user(userScott)
                .build();
        quantumConf.getChairs().add(scottChair);
        quantumConf.getPhases().addAll(new ArrayList<>(List.of(biddingPhaseQuant, registrationPhaseQuant,
                reviewingPhaseQuant, submittingPhaseQuant, session_registrationPhaseQuant)));
        conferenceRepo.save(quantumConf);
    }

}
