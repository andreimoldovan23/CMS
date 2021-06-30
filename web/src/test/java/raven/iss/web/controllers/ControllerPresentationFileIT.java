package raven.iss.web.controllers;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import raven.iss.web.controllers.Utils.AbstractRestControllerTest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static raven.iss.web.controllers.Utils.LogInUtils.getTokenForLogin;

public class ControllerPresentationFileIT extends AbstractRestControllerTest {
    private static final String filePath = "E:\\Projects\\Faculta\\SE\\CMS\\CMS\\FileStorage\\TheIroniesofLiberation.pdf";
    private static final String username = "Greg";
    private static final String password = "Greg_2#?";
    private String token;
    private String name;
    private String originalFileName;
    private String contentType;
    private byte[] content;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
        try {
            token = getTokenForLogin(username, password, getMockMvc());
            name = "presentationFile";
            originalFileName = "TheIroniesofLiberation.pdf";
            contentType = "application/pdf";
            content = FileUtils.readFileToByteArray(new File(filePath));
        } catch (Exception e) {
            token = null;
            content = null;
        }
    }

    @Test
    public void uploadAndGetTest() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile(name, originalFileName, contentType,
                        content);

        getMockMvc().perform(multipart("/api/conferences/2/papers/2/presentationFile").file(multipartFile)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        MockHttpServletResponse response = getMockMvc().perform(get("/api/conferences/2/papers/2/presentationFile")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andReturn().getResponse();

        byte[] responseBytes = response.getContentAsByteArray();
        assertEquals(content.length, responseBytes.length);
    }
}
