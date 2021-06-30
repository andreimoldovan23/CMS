package raven.iss.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.LoginDTO;
import raven.iss.data.api.mappers.UserMapper;
import raven.iss.data.model.User;
import raven.iss.web.controllers.Utils.AbstractRestControllerTest;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static raven.iss.web.controllers.Utils.LogInUtils.getTokenForLogin;

public class UserControllerIT extends AbstractRestControllerTest {

    private static final String username = "Greg";
    private static final String password = "Greg_2#?";
    private static final String adminUsername = "Root";
    private static final String adminPassword = "root_Admin_2#";
    private static final String userUrl = "/api/user";
    private static final String newUserUrl = "/api/user/new";
    private static final String userTypeUrl = "/api/user/type";
    private static final String secondConfUserTypeUrl = "/api/conferences/2/user/type";
    private static final String firstConfUserTypeUrl = "/api/conferences/1/user/type";
    private static final String firstConfRegisterAuthor = "/api/conferences/1/registerAsAuthor";
    private static final String firstConfRegisterListener = "/api/conferences/1/registerAsListener";
    private static final String fourthConfRegisterAuthor = "/api/conferences/4/registerAsAuthor";
    private static final String fourthConfRegisterListener = "/api/conferences/4/registerAsListener";
    private static final String thirdConfRegisterListener = "/api/conferences/3/registerAsListener";
    private static final String thirdConfRegisterAuthor = "/api/conferences/3/registerAsAuthor";

    private static String jsonBody;
    private static String gregUserJson;
    private static String userRoleJson;
    private static String emptyRoleJson;
    private static String userAdminRoleJson;
    private static String authorListenerChairRoleJson;
    private static String authorRoleJson;
    private static String listenerRoleJson;

    private static final UserMapper userMapper = UserMapper.INSTANCE;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        LoginDTO newUser = new LoginDTO();
        newUser.setUsername("INew");
        newUser.setName("New name");
        newUser.setPassword("New_Pass_2");
        newUser.setMail("new_mail@gmail.com");
        newUser.setJob("new job");
        newUser.setPhoneNumber("+40723444567");
        jsonBody = objectMapper.writeValueAsString(newUser);

        User gregUser = User.builder().username("Greg").name("Greg").mail("greg@gmail.com").build();
        gregUserJson = objectMapper.writeValueAsString(userMapper.userToDTO(gregUser));

        UserController.Role userRole = new UserController.Role(new ArrayList<>(List.of("USER")));
        UserController.Role emptyRole = new UserController.Role(new ArrayList<>());
        UserController.Role userAdminRole = new UserController.Role(new ArrayList<>(List.of("USER", "ADMIN")));
        UserController.Role authorListenerChairRole = new UserController.Role(new ArrayList<>(List.of("AUTHOR", "LISTENER", "CHAIR")));
        UserController.Role authorRole = new UserController.Role(new ArrayList<>(List.of("AUTHOR")));
        UserController.Role listenerRole = new UserController.Role(new ArrayList<>(List.of("LISTENER")));

        userRoleJson = objectMapper.writeValueAsString(userRole);
        emptyRoleJson = objectMapper.writeValueAsString(emptyRole);
        userAdminRoleJson = objectMapper.writeValueAsString(userAdminRole);
        authorListenerChairRoleJson = objectMapper.writeValueAsString(authorListenerChairRole);
        authorRoleJson = objectMapper.writeValueAsString(authorRole);
        listenerRoleJson = objectMapper.writeValueAsString(listenerRole);
    }

    @Test
    public void getActualUserForUserWithToken() throws Exception {
        final String token = getTokenForLogin(username, password, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(gregUserJson).build();
        performGetRequest(data, userUrl, status().isOk());
    }

    @Test
    public void getActualUserForUserWithoutToken() throws Exception {
        RequestData data = RequestData.builder().build();
        performGetRequest(data, userUrl, status().isUnauthorized());
    }

    @Test
    public void signUpTest() throws Exception {
        RequestData data = RequestData.builder().content(jsonBody).build();
        performPostRequest(data, newUserUrl, status().isOk());
        performPostRequest(data, newUserUrl, status().is5xxServerError());
    }

    @Test
    public void getUserTypeTest() throws Exception {
        final String token = getTokenForLogin(username, password, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(userRoleJson).build();
        performGetRequest(data, userTypeUrl, status().isOk());
    }

    @Test
    public void getUserConferenceRolesTest() throws Exception {
        final String token = getTokenForLogin(username, password, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(authorListenerChairRoleJson).build();
        performGetRequest(data, secondConfUserTypeUrl, status().isOk());

        data.setResultContent(emptyRoleJson);
        performGetRequest(data, firstConfUserTypeUrl, status().isOk());
    }

    @Test
    public void getUserAdminTypeTest() throws Exception {
        final String token = getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(userAdminRoleJson).build();
        performGetRequest(data, userTypeUrl, status().isOk());
    }

    @Test
    public void registerAuthorTest() throws Exception {
        String token = getTokenForLogin(username, password, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(emptyRoleJson).build();
        performGetRequest(data, firstConfUserTypeUrl, status().isOk());

        data = RequestData.builder().token(token).build();
        performPostRequest(data, firstConfRegisterAuthor, status().isOk());

        token = getTokenForLogin(username, password, getMockMvc());
        data = RequestData.builder().token(token).resultContent(authorRoleJson).build();
        performGetRequest(data, firstConfUserTypeUrl, status().isOk());

        data = RequestData.builder().token(token).build();
        performPostRequest(data, firstConfRegisterAuthor, status().is5xxServerError());
    }

    @Test
    public void registerListenerTest() throws Exception {
        String token = getTokenForLogin(username, password, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(emptyRoleJson).build();
        performGetRequest(data, firstConfUserTypeUrl, status().isOk());

        data = RequestData.builder().token(token).build();
        performPostRequest(data, firstConfRegisterListener, status().isOk());

        token = getTokenForLogin(username, password, getMockMvc());
        data = RequestData.builder().token(token).resultContent(listenerRoleJson).build();
        performGetRequest(data, firstConfUserTypeUrl, status().isOk());

        data = RequestData.builder().token(token).build();
        performPostRequest(data, firstConfRegisterListener, status().is5xxServerError());
    }

    @Test
    public void registerAsListenerNotActive() throws Exception {
        final String token = getTokenForLogin(username, password, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performPostRequest(data, thirdConfRegisterListener, status().is5xxServerError());
    }

    @Test
    public void registerAsAuthorNotActive() throws Exception {
        final String token = getTokenForLogin(username, password, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performPostRequest(data, thirdConfRegisterAuthor, status().is5xxServerError());
    }

    @Test
    public void registerAsListenerAfterDeadline() throws Exception {
        final String token = getTokenForLogin(username, password, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performPostRequest(data, fourthConfRegisterListener, status().is5xxServerError());
    }

    @Test
    public void registerAsAuthorAfterDeadline() throws Exception {
        final String token = getTokenForLogin(username, password, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performPostRequest(data, fourthConfRegisterAuthor, status().is5xxServerError());
    }

}
