package raven.iss.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.LoginDTO;
import raven.iss.web.controllers.Utils.AbstractRestControllerTest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationIT extends AbstractRestControllerTest {

    private static final String normalUserUsername = "Greg";
    private static final String normalUserPassword = "Greg_2#?";
    private static final String adminUsername = "Root";
    private static final String adminPassword = "root_Admin_2#";
    private static final String wrongUsername = "Mandinga";
    private static final String wrongPassword = "Whiskey_45";
    private static final String authenticateUrl = "/api/authenticate";

    private static String normalUserJson;
    private static String adminJson;
    private static String wrongUserJson;
    private static String wrongUserJson2;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        LoginDTO normalLogin = new LoginDTO();
        normalLogin.setUsername(normalUserUsername);
        normalLogin.setPassword(normalUserPassword);

        LoginDTO adminLogin = new LoginDTO();
        adminLogin.setUsername(adminUsername);
        adminLogin.setPassword(adminPassword);

        LoginDTO wrongLogin1 = new LoginDTO();
        wrongLogin1.setUsername(normalUserUsername);
        wrongLogin1.setPassword(wrongPassword);

        LoginDTO wrongLogin2 = new LoginDTO();
        wrongLogin2.setUsername(wrongUsername);
        wrongLogin2.setPassword(normalUserPassword);

        normalUserJson = objectMapper.writeValueAsString(normalLogin);
        adminJson = objectMapper.writeValueAsString(adminLogin);
        wrongUserJson = objectMapper.writeValueAsString(wrongLogin1);
        wrongUserJson2 = objectMapper.writeValueAsString(wrongLogin2);
    }

    @Test
    public void successfulAuthenticationWithUser() throws Exception {
        RequestData data = RequestData.builder().content(normalUserJson).build();
        performPostRequest(data, authenticateUrl, status().isOk());
    }

    @Test
    public void successfulAuthenticationWithAdmin() throws Exception {
        RequestData data = RequestData.builder().content(adminJson).build();
        performPostRequest(data, authenticateUrl, status().isOk());
    }

    @Test
    public void unsuccessfulAuthenticationWithWrongPassword() throws Exception {
        RequestData data = RequestData.builder().content(wrongUserJson).build();
        performPostRequest(data, authenticateUrl, status().isUnauthorized());
    }

    @Test
    public void unsuccessfulAuthenticationWithNotExistingUser() throws Exception {
        RequestData data = RequestData.builder().content(wrongUserJson2).build();
        performPostRequest(data, authenticateUrl, status().is4xxClientError());
    }

}
