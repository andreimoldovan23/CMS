package raven.iss.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ChairDTO;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.api.dtos.PCMemberDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.api.mappers.ConferenceMapper;
import raven.iss.data.api.mappers.UserMapper;
import raven.iss.data.model.Conference;
import raven.iss.data.model.User;
import raven.iss.web.controllers.Utils.AbstractRestControllerTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static raven.iss.web.controllers.Utils.LogInUtils.getTokenForLogin;

public class ConferenceControllerIT extends AbstractRestControllerTest {

    private static final String adminUsername = "Root";
    private static final String adminPassword = "root_Admin_2#";
    private static final String chairUsername = "Greg";
    private static final String chairPassword = "Greg_2#?";
    private static final String pcMemberUsername = "Amy";
    private static final String pcMemberPassword = "Greg_2#?";
    private static final String idString = "{\"id\": 5}";
    private static final String confUrl = "/api/conferences";
    private static final String firstConfUrl = "/api/conferences/1";
    private static final String secondConfUrl = "/api/conferences/2";
    private static final String firstConfChairUrl = "/api/conferences/1/chairs";
    private static final String secondConfChairUrl = "/api/conferences/2/chairs";
    private static final String secondConfFilterUrl = "/api/conferences/2/filter";
    private static final String secondConfPcMembersUrl = "/api/conferences/2/pcmembers";
    private static final String firstConfRoleUrl = "/api/conferences/1/user/type";
    private static final String secondConfRoleUrl = "/api/conferences/2/user/type";
    private static final String secondConfBiddingUrl = "/api/conferences/2/pcmembers/bidding";
    private static final String secondConfReviewersUrl = "/api/conferences/2/papers/2/reviewers";
    private static final String phaseStartUrl = "/api/conferences/2/phases/2/start";
    private static final String pcmembersNotAssignedPaperUrl = "/api/conferences/2/papers/1/pcmembersNotAssigned";
    private static final String pcMemberHasBiddedUrl = "/api/conferences/2/pcmembers/hasBidded";

    private static final ConferenceMapper conferenceMapper = ConferenceMapper.INSTANCE;
    private static final UserMapper userMapper = UserMapper.INSTANCE;

    private static String jsonBody;
    private static String wrongJsonBody;
    private static String firstConf;
    private static String allConfs;
    private static String chairDto;
    private static String chairListDto;
    private static String userMike;
    private static String listUsers;
    private static String listPcMembers;
    private static String listPcMembers2;
    private static String emptyString;
    private static String chairRole;
    private static String authorMemberRoles;
    private static String listPcMembersNotAssigned;
    private static String amyPcMemberJson;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        ConferenceDTO conf1 = conferenceMapper.conferenceToDTO(Conference.builder()
                .name("QuantumMechanics")
                .city("NewYork")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build());
        conf1.setId(1);

        ConferenceDTO conf2 = conferenceMapper.conferenceToDTO(Conference.builder()
                .name("AllStar Conference")
                .city("Cluj")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build());
        conf2.setId(2);

        ConferenceDTO conf3 = conferenceMapper.conferenceToDTO(Conference.builder()
                .name("Retro")
                .city("LosAngeles")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build());
        conf3.setId(3);

        ConferenceDTO conf4 = conferenceMapper.conferenceToDTO(Conference.builder()
                .name("IceCream")
                .city("Helsinki")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build());
        conf4.setId(4);

        ConferenceDTO toAdd = conferenceMapper.conferenceToDTO(Conference.builder()
                .name("Soccer")
                .city("NewYork")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build());

        ConferenceDTO wrongToAdd = conferenceMapper.conferenceToDTO(Conference.builder()
                .name("Soccer")
                .city("NewYork")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2000, 8, 4, 4, 4, 4))
                .build());

        UserDTO user = userMapper.userToDTO(User.builder().username("Greg").build());
        UserDTO mikeUser = userMapper.userToDTO(User.builder().username("Mike").build());
        UserDTO amyUser = userMapper.userToDTO(User.builder().username("Amy").build());

        ChairDTO chair = new ChairDTO();
        chair.setUser(user);
        ChairDTO mikeChair = new ChairDTO();
        mikeChair.setUser(mikeUser);

        PCMemberDTO amyPcMember = new PCMemberDTO();
        amyPcMember.setUser(amyUser);
        amyPcMember.setConf(conf2);
        amyPcMember.setLikeToReview(true);

        PCMemberDTO mikePcMember = new PCMemberDTO();
        mikePcMember.setUser(mikeUser);
        mikePcMember.setConf(conf2);
        mikePcMember.setLikeToReview(false);

        jsonBody = objectMapper.writeValueAsString(toAdd);
        wrongJsonBody = objectMapper.writeValueAsString(wrongToAdd);
        firstConf = objectMapper.writeValueAsString(conf1);
        allConfs = objectMapper.writeValueAsString(new ArrayList<>(List.of(conf1, conf2, conf3, conf4)));
        chairDto = objectMapper.writeValueAsString(chair);
        chairListDto = objectMapper.writeValueAsString(new ArrayList<>(List.of(chair)));
        userMike = objectMapper.writeValueAsString(mikeChair);
        listUsers = objectMapper.writeValueAsString(new ArrayList<>(List.of(mikeUser)));
        listPcMembers = objectMapper.writeValueAsString(new ArrayList<>(List.of(amyPcMember, mikePcMember)));
        listPcMembersNotAssigned = objectMapper.writeValueAsString(new ArrayList<>(List.of(amyPcMember)));

        mikePcMember.setLikeToReview(true);
        listPcMembers2 = objectMapper.writeValueAsString(new ArrayList<>(List.of(amyPcMember, mikePcMember)));

        emptyString = objectMapper.writeValueAsString(new ArrayList<>());

        chairRole = objectMapper.writeValueAsString(new UserController.Role(new ArrayList<>(List.of("CHAIR"))));
        authorMemberRoles = objectMapper.writeValueAsString(new UserController.Role(new ArrayList<>(List.of("AUTHOR", "PCMEMBER"))));

        amyPcMemberJson = objectMapper.writeValueAsString(amyPcMember);
    }

    @Test
    public void getOneTest() throws Exception {
        RequestData data = RequestData.builder().resultContent(firstConf).build();
        performGetRequest(data, firstConfUrl, status().isOk());
    }

    @Test
    public void getAllTest() throws Exception {
        RequestData data = RequestData.builder().resultContent(allConfs).build();
        performGetRequest(data, confUrl, status().isOk());
    }

    @Test
    public void addTest() throws Exception {
        final String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token)
                .content(jsonBody).resultContent(idString).build();
        performPostRequest(data, confUrl, status().isOk());
    }

    @Test
    public void addWrongDates() throws Exception {
        final String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token)
                .content(wrongJsonBody).build();
        performPostRequest(data, confUrl, status().is5xxServerError());
    }

    @Test
    public void addUnauthorized() throws Exception {
        final String token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token)
                .content(jsonBody).build();
        performPostRequest(data, confUrl, status().isForbidden());
    }

    @Test
    public void editTest() throws Exception {
        final String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).content(jsonBody).build();
        performPutRequest(data, firstConfUrl, status().isOk());
    }

    @Test
    public void editWrongDates() throws Exception {
        final String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).content(wrongJsonBody).build();
        performPutRequest(data, firstConfUrl, status().is5xxServerError());
    }

    @Test
    public void editUnauthorized() throws Exception {
        final String token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).content(jsonBody).build();
        performPutRequest(data, firstConfUrl, status().isForbidden());
    }

    @Test
    public void deleteTest() throws Exception {
        final String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performDeleteRequest(data, secondConfUrl, status().isOk());
    }

    @Test
    public void deleteUnauthorized() throws Exception {
        final String token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performDeleteRequest(data, firstConfUrl, status().isForbidden());
    }

    @Test
    public void addChairTest() throws Exception {
        String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).content(chairDto).build();
        performPostRequest(data, firstConfChairUrl, status().isOk());

        token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).resultContent(chairRole).build();
        performGetRequest(data, firstConfRoleUrl, status().isOk());

        token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).content(chairDto).build();
        performPostRequest(data, firstConfChairUrl, status().is5xxServerError());
    }

    @Test
    public void getAllChairsTest() throws Exception {
        final String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).resultContent(chairListDto).build();
        performGetRequest(data, secondConfChairUrl, status().isOk());
    }

    @Test
    public void deleteChairTest() throws Exception {
        final String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).paramKey("username").paramValue(chairUsername).build();
        performDeleteRequest(data, secondConfChairUrl, status().isOk());
        data = RequestData.builder()
                .token(token).resultContent(emptyString).build();
        performGetRequest(data, secondConfChairUrl, status().isOk());
    }

    @Test
    public void filterNotChairNotPC() throws Exception {
        final String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).resultContent(listUsers).build();
        performGetRequest(data, secondConfFilterUrl, status().isOk());
    }

    @Test
    public void addChairUnauthorized() throws Exception {
        final String token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).content(chairDto).build();
        performPostRequest(data, firstConfChairUrl, status().isForbidden());
    }

    @Test
    public void pcMemberOpsTest() throws Exception {
        String token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).content(userMike).build();
        performPostRequest(data, secondConfPcMembersUrl, status().isOk());

        //check that the pc member was added properly
        token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).resultContent(listPcMembers).build();
        performGetRequest(data, secondConfPcMembersUrl, status().isOk());

        //check that now that user also has PCMEMBER authority
        token = getTokenForLogin("Mike", chairPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).resultContent(authorMemberRoles).build();
        performGetRequest(data, secondConfRoleUrl, status().isOk());

        //check that the same user cannot be added twice at the same conference as pcmember
        token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).content(userMike).build();
        performPostRequest(data, secondConfPcMembersUrl, status().is5xxServerError());
    }

    @Test
    public void getPcMembersNotAssignedSpecificPaperTest() throws Exception {
        String token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token)
                .resultContent(listPcMembersNotAssigned)
                .build();
        performGetRequest(data, pcmembersNotAssignedPaperUrl, status().isOk());

    }

    @Test
    public void biddingAndAssigningTest() throws Exception {
        String token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).content(userMike).build();
        performPostRequest(data, secondConfPcMembersUrl, status().isOk());

        //try to bid while bidding not active
        token = getTokenForLogin("Mike", chairPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).paramKey("available").paramValue("true").build();
        performPutRequest(data, secondConfBiddingUrl, status().is5xxServerError());

        //try to assign paper to Greg when he is not pleased to review
        token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).content(userMike).build();
        performPostRequest(data, secondConfReviewersUrl, status().is5xxServerError());

        //make bidding active
        data = RequestData.builder()
                .token(token).build();
        performPutRequest(data, phaseStartUrl, status().isOk());

        //try to bid
        token = getTokenForLogin("Mike", chairPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).paramKey("available").paramValue("true").build();
        performPutRequest(data, secondConfBiddingUrl, status().isOk());

        //try to assign paper to Greg when he is pleased to review
        token = getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).content(userMike).build();
        performPostRequest(data, secondConfReviewersUrl, status().isOk());

        //check that bidding works properly
        data = RequestData.builder()
                .token(token).resultContent(listPcMembers2).build();
        performGetRequest(data, secondConfPcMembersUrl, status().isOk());
    }

    @Test
    public void checkIfPcMemberBiddedTest() throws Exception {
        String token = getTokenForLogin(pcMemberUsername, pcMemberPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).resultContent(amyPcMemberJson).build();
        performGetRequest(data, pcMemberHasBiddedUrl, status().isOk());
    }

}
