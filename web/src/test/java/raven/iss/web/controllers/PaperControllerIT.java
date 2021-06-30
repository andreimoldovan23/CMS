package raven.iss.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ChairDTO;
import raven.iss.data.api.dtos.PaperDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.api.mappers.PaperMapper;
import raven.iss.data.api.mappers.UserMapper;
import raven.iss.data.model.Paper;
import raven.iss.data.model.Status;
import raven.iss.data.model.User;
import raven.iss.web.controllers.Utils.AbstractRestControllerTest;
import raven.iss.web.controllers.Utils.LogInUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaperControllerIT extends AbstractRestControllerTest {

    private static final String authorUsername = "Greg";
    private static final String authorPassword = "Greg_2#?";
    private static final String pcMemberUsername = "Amy";
    private static final String pcMemberPassword = "Greg_2#?";
    private static final String secondConfPapers = "/api/conferences/2/papers";
    private static final String secondConfThirdPaper = "/api/conferences/2/papers/3";
    private static final String secondConfSecondPaper = "/api/conferences/2/papers/2";
    private static final String secondConfAuthors = "/api/conferences/2/papers/2/authors";
    private static final String pcMembersPapersToReview = "/api/conferences/2/papersToReview";
    private static final String allPapersOfAuthorUrl = "/api/conferences/2/papersOfAuthor";

    private static String addPaperJson;
    private static String updatePaperJson;
    private static String authorJson;
    private static String addedPaperJson;
    private static String updatedPaperJson;
    private static String secondPaperJson;
    private static String listPapersJson;
    private static String listAuthorsSingleJson;
    private static String listAuthorsManyJson;
    private static String listPapersToReview;
    private static String arrayJsonSecondPaper;

    private static final UserMapper userMapper = UserMapper.INSTANCE;
    private static final PaperMapper paperMapper = PaperMapper.INSTANCE;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        PaperDTO toAddPaperDTO = paperMapper.paperToDTO(Paper.builder()
                .name("test")
                .topics(new HashSet<>(List.of("topic1", "topic2")))
                .keywords(new HashSet<>(List.of("keyword1", "keyword2")))
                .build());

        PaperDTO toUpdatePaperDto = paperMapper.paperToDTO(Paper.builder()
                .name("test")
                .topics(new HashSet<>(List.of("topic5", "topic6")))
                .keywords(new HashSet<>(List.of("keyword5", "keyword6")))
                .build());

        PaperDTO secondPaper = paperMapper.paperToDTO(Paper.builder()
                .name("The Ironies of Liberation").build());
        secondPaper.setId(2);
        secondPaper.setStatus(Status.inReview);

        PaperDTO firstPaper = paperMapper.paperToDTO(Paper.builder()
                .name("Vlogging for dummies").build());
        firstPaper.setId(1);
        firstPaper.setStatus(Status.inReview);

        UserDTO mikeUser = userMapper.userToDTO(User.builder().username("Mike").build());
        ChairDTO mikeChair = new ChairDTO();
        mikeChair.setUser(mikeUser);

        UserDTO gregUser = userMapper.userToDTO(User.builder().username("Greg").build());
        ChairDTO gregChair = new ChairDTO();
        gregChair.setUser(gregUser);

        addPaperJson = objectMapper.writeValueAsString(toAddPaperDTO);
        updatePaperJson = objectMapper.writeValueAsString(toUpdatePaperDto);
        authorJson = objectMapper.writeValueAsString(mikeChair);

        toAddPaperDTO.setId(3);
        toAddPaperDTO.setStatus(Status.inReview);
        addedPaperJson = objectMapper.writeValueAsString(toAddPaperDTO);

        toUpdatePaperDto.setId(2);
        toUpdatePaperDto.setStatus(Status.inReview);
        updatedPaperJson = objectMapper.writeValueAsString(toUpdatePaperDto);

        secondPaperJson = objectMapper.writeValueAsString(secondPaper);
        listPapersJson = objectMapper.writeValueAsString(new ArrayList<>(List.of(firstPaper, secondPaper)));
        listAuthorsSingleJson = objectMapper.writeValueAsString(new ArrayList<>(List.of(gregChair)));
        listAuthorsManyJson = objectMapper.writeValueAsString(new ArrayList<>(List.of(mikeChair, gregChair)));
        listPapersToReview = objectMapper.writeValueAsString(new ArrayList<>(List.of(secondPaper)));

        arrayJsonSecondPaper = objectMapper.writeValueAsString(new ArrayList<>(List.of(secondPaper)));
    }


    @Test
    public void addPaper() throws Exception {
        final String token = LogInUtils.getTokenForLogin(authorUsername, authorPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).content(addPaperJson).build();
        performPostRequest(data, secondConfPapers, status().isOk());

        data = RequestData.builder()
                .token(token).resultContent(addedPaperJson).build();
        performGetRequest(data, secondConfThirdPaper, status().isOk());
    }

    @Test
    public void updatePaper() throws Exception {
        String token = LogInUtils.getTokenForLogin("Mike", authorPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).content(updatePaperJson).build();
        performPutRequest(data, secondConfSecondPaper, status().isForbidden());

        token = LogInUtils.getTokenForLogin(authorUsername, authorPassword, getMockMvc());
        data = RequestData.builder()
                .token(token).content(updatePaperJson).build();
        performPutRequest(data, secondConfSecondPaper, status().isOk());

        data = RequestData.builder()
                .token(token).resultContent(updatedPaperJson).build();
        performGetRequest(data, secondConfSecondPaper, status().isOk());
    }

    @Test
    public void deletePaper() throws Exception {
        String token = LogInUtils.getTokenForLogin("Mike", authorPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performDeleteRequest(data, secondConfSecondPaper, status().isForbidden());

        token = LogInUtils.getTokenForLogin(authorUsername, authorPassword, getMockMvc());
        data = RequestData.builder().token(token).resultContent(secondPaperJson).build();
        performGetRequest(data, secondConfSecondPaper, status().isOk());

        data = RequestData.builder().token(token).build();
        performDeleteRequest(data, secondConfSecondPaper, status().isOk());

        performGetRequest(data, secondConfSecondPaper, status().is4xxClientError());
    }

    @Test
    public void getAll() throws Exception {
        final String token = LogInUtils.getTokenForLogin(authorUsername, authorPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(listPapersJson).build();
        performGetRequest(data, secondConfPapers, status().isOk());
    }

    @Test
    public void getPCMemberPapersToReviewTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(pcMemberUsername, pcMemberPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token)
                .resultContent(listPapersToReview)
                .build();
        performGetRequest(data, pcMembersPapersToReview, status().isOk());
    }

    @Test
    public void addAuthorTest() throws Exception {
        String token = LogInUtils.getTokenForLogin("Mike", authorPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();

        data.setContent(authorJson);
        performPutRequest(data, secondConfAuthors, status().isForbidden());

        token = LogInUtils.getTokenForLogin(authorUsername, authorPassword, getMockMvc());
        data = RequestData.builder().token(token).resultContent(listAuthorsSingleJson).build();
        performGetRequest(data, secondConfAuthors, status().isOk());

        data = RequestData.builder().token(token).content(authorJson).build();
        performPutRequest(data, secondConfAuthors, status().isOk());

        data = RequestData.builder().token(token).resultContent(listAuthorsManyJson).build();
        performGetRequest(data, secondConfAuthors, status().isOk());
    }

    @Test
    public void getAllPapersOfAuthorTest() throws Exception {
        String token = LogInUtils.getTokenForLogin(authorUsername, authorPassword, getMockMvc());
        RequestData data = RequestData.builder()
                .token(token).resultContent(arrayJsonSecondPaper).build();
        performGetRequest(data, allPapersOfAuthorUrl, status().isOk());
    }
}
