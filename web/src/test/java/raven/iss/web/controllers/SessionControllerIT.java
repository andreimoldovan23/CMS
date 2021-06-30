package raven.iss.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.mappers.ChairMapper;
import raven.iss.data.api.mappers.SessionMapper;
import raven.iss.data.api.mappers.UserMapper;
import raven.iss.data.model.*;
import raven.iss.web.controllers.Utils.AbstractRestControllerTest;
import raven.iss.web.controllers.Utils.LogInUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SessionControllerIT extends AbstractRestControllerTest {

    private static final String adminUsername = "Root";
    private static final String adminPassword = "root_Admin_2#";
    private static final String gregUsername = "Greg";
    private static final String gregPassword = "Greg_2#?";
    private static final String secondConfFirstSession = "/api/conferences/2/sessions/1";
    private static final String secondConfSessions = "/api/conferences/2/sessions";
    private static final String fourthConfRegister = "/api/conferences/4/sessions/2/register";
    private static final String secondConfSpeakers = "/api/conferences/2/sessions/1/speakers";
    private static final String secondConfAttendsSession = "/api/conferences/2/sessions/1/isAttending";

    private static String addSessionJson;
    private static String speakerSessionJson;
    private static String speakerBadSessionJson1;
    private static String speakerBadSessionJson2;
    private static String speakerBadSessionJson3;
    private static String updateSessionJson;
    private static String firstSessionJson;
    private static String thirdSessionJson;
    private static String firstSessionUpdatedJson;
    private static String listUsers;
    private static String attendsSessionJson;

    private static final SessionMapper sessionMapper = SessionMapper.INSTANCE;
    private static final ChairMapper chairMapper = ChairMapper.INSTANCE;
    private static final UserMapper userMapper = UserMapper.INSTANCE;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        User mirceaUser = User.builder().username("Mircea").build();
        User mikeUser = User.builder().username("Mike").build();
        User amyUser = User.builder().username("Amy").build();
        User gregUser = User.builder().username("Greg").build();

        Chair mirceaChair = Chair.builder().user(mirceaUser).build();
        Chair mikeChair = Chair.builder().user(mikeUser).build();
        Chair amyChair = Chair.builder().user(amyUser).build();
        Chair gregChair = Chair.builder().user(gregUser).build();

        speakerSessionJson = objectMapper.writeValueAsString(chairMapper.chairToDTO(amyChair));
        speakerBadSessionJson1 = objectMapper.writeValueAsString(chairMapper.chairToDTO(gregChair));
        speakerBadSessionJson2 = objectMapper.writeValueAsString(chairMapper.chairToDTO(mikeChair));
        speakerBadSessionJson3 = objectMapper.writeValueAsString(chairMapper.chairToDTO(mirceaChair));

        Room platoRoom = Room.builder().name("Plato").build();
        Session updateSession = Session.builder().name("testNew").topic("testNew").build();
        Session addSession = Session.builder().name("test").topic("test").chair(gregChair).room(platoRoom).build();
        updateSessionJson = objectMapper.writeValueAsString(sessionMapper.sessionToDTO(updateSession));
        addSessionJson = objectMapper.writeValueAsString(sessionMapper.sessionToDTO(addSession));

        Conference conf = Conference.builder().name("AllStar Conference")
                .city("Cluj")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build();

        Session firstSession = Session.builder().name("How to become famous?").topic("social")
                .conf(conf).build();
        firstSession.setId(1);
        firstSessionJson = objectMapper.writeValueAsString(sessionMapper.sessionToDTO(firstSession));

        Session thirdSession = Session.builder().name("test").topic("test").conf(conf).build();
        thirdSession.setId(3);
        thirdSessionJson = objectMapper.writeValueAsString(sessionMapper.sessionToDTO(thirdSession));

        firstSession.setName("testNew");
        firstSession.setTopic("testNew");
        firstSessionUpdatedJson = objectMapper.writeValueAsString(sessionMapper.sessionToDTO(firstSession));

        listUsers = objectMapper.writeValueAsString(new ArrayList<>(List.of(userMapper.userToDTO(mikeUser))));

        SessionController.AttendsData data = new SessionController.AttendsData(false);
        attendsSessionJson = objectMapper.writeValueAsString(data);
    }

    @Test
    public void getSession() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(firstSessionJson).build();
        performGetRequest(data, secondConfFirstSession, status().isOk());
    }

    @Test
    public void addSession() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(addSessionJson).resultContent(thirdSessionJson).build();
        performPostRequest(data, secondConfSessions, status().isOk());

        data = RequestData.builder().token(token).content(addSessionJson).build();
        performPostRequest(data, secondConfSessions, status().is5xxServerError());
    }

    @Test
    public void updateSession() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(updateSessionJson).build();
        performPutRequest(data, secondConfFirstSession, status().isOk());

        data = RequestData.builder().token(token).resultContent(firstSessionUpdatedJson).build();
        performGetRequest(data, secondConfFirstSession, status().isOk());
    }

    @Test
    public void deleteSession() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(firstSessionJson).build();
        performGetRequest(data, secondConfFirstSession, status().isOk());

        data = RequestData.builder().token(token).build();
        performDeleteRequest(data, secondConfFirstSession, status().isOk());

        performGetRequest(data, secondConfFirstSession, status().is4xxClientError());
    }

    @Test
    public void attendSession() throws Exception {
        final String token = LogInUtils.getTokenForLogin(gregUsername, gregPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performPutRequest(data, fourthConfRegister, status().isOk());
        performPutRequest(data, fourthConfRegister, status().is5xxServerError());
    }

    @Test
    public void getSpeakersTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(gregUsername, gregPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(listUsers).build();
        performGetRequest(data, secondConfSpeakers, status().isOk());
    }

    @Test
    public void addSpeakerToSession() throws Exception {
        final String token = LogInUtils.getTokenForLogin(gregUsername, gregPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(speakerBadSessionJson1).build();
        performPostRequest(data, secondConfSpeakers, status().is5xxServerError());

        data.setContent(speakerBadSessionJson2);
        performPostRequest(data, secondConfSpeakers, status().is5xxServerError());

        data.setContent(speakerBadSessionJson3);
        performPostRequest(data, secondConfSpeakers, status().is4xxClientError());

        data.setContent(speakerSessionJson);
        performPostRequest(data, secondConfSpeakers, status().isOk());
    }

    @Test
    public void deleteSpeakerFromSession() throws Exception {
        final String token = LogInUtils.getTokenForLogin(gregUsername, gregPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).paramKey("username").paramValue("Mircea").build();
        performDeleteRequest(data, secondConfSpeakers, status().is4xxClientError());

        data.setParamValue("Greg");
        performDeleteRequest(data, secondConfSpeakers, status().is4xxClientError());

        data.setParamValue("Mike");
        performDeleteRequest(data, secondConfSpeakers, status().isOk());
    }

    @Test
    public void isAttendingSessionTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(gregUsername, gregPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(attendsSessionJson).build();
        performGetRequest(data, secondConfAttendsSession, status().isOk());
    }

}
