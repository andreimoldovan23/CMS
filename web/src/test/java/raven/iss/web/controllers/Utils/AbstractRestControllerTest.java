package raven.iss.web.controllers.Utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public abstract class AbstractRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setUpClass() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public void performGetRequest(RequestData data, String url, ResultMatcher resultMatcher) throws Exception {
        getMockMvc().perform(get(url)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + data.getToken())
            .content(data.getContent())
            .param(data.getParamKey(), data.getParamValue()))
                .andExpect(resultMatcher)
                .andExpect(!data.getResultContent().equals("") ?
                        content().json(data.getResultContent()) : content().string(containsString("")))
                .andReturn().getResponse();
    }

    public void performPostRequest(RequestData data, String url, ResultMatcher resultMatcher) throws Exception {
        getMockMvc().perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + data.getToken())
                .content(data.getContent())
                .param(data.getParamKey(), data.getParamValue()))
                .andExpect(resultMatcher)
                .andExpect(!data.getResultContent().equals("") ?
                        content().json(data.getResultContent()) : content().string(containsString("")))
                .andReturn().getResponse();
    }

    public void performPutRequest(RequestData data, String url, ResultMatcher resultMatcher) throws Exception {
        getMockMvc().perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + data.getToken())
                .content(data.getContent())
                .param(data.getParamKey(), data.getParamValue()))
                .andExpect(resultMatcher)
                .andExpect(!data.getResultContent().equals("") ?
                        content().json(data.getResultContent()) : content().string(containsString("")))
                .andReturn().getResponse();
    }

    public void performDeleteRequest(RequestData data, String url, ResultMatcher resultMatcher) throws Exception {
        getMockMvc().perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + data.getToken())
                .content(data.getContent())
                .param(data.getParamKey(), data.getParamValue()))
                .andExpect(resultMatcher)
                .andExpect(!data.getResultContent().equals("") ?
                        content().json(data.getResultContent()) : content().string(containsString("")))
                .andReturn().getResponse();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RequestData {
        @Builder.Default
        private String token = "token";

        @Builder.Default
        private String content = "{content}";

        @Builder.Default
        private String paramKey = "key";

        @Builder.Default
        private String paramValue = "value";

        @Builder.Default
        private String resultContent = "";
    }

}
