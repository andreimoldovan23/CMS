package raven.iss.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.PaperDTO;
import raven.iss.data.api.dtos.ReviewDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.model.Grade;
import raven.iss.data.model.Status;
import raven.iss.web.controllers.Utils.AbstractRestControllerTest;
import raven.iss.web.controllers.Utils.LogInUtils;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewControllerIT extends AbstractRestControllerTest {
    private static final String pcMemberUsername = "Amy";
    private static final String pcMemberPassword = "Greg_2#?";
    private static final String nonReviewerUsername = "Mike";
    private static final String nonReviewerPassword = "Greg_2#?";
    private static final String ownPaperReviewerUsername = "Greg";
    private static final String ownPaperReviewerPassword = "Greg_2#?";
    private static final String chairUsername = "Greg";
    private static final String chairPassword = "Greg_2#?";
    private static final String secondConfSecondPaperAccepted = "/api/conferences/2/papers/2/accepted";
    private static final String secondConfFirstPaperAccepted = "/api/conferences/2/papers/1/accepted";
    private static final String secondConfFirstPaper = "/api/conferences/2/papers/1";
    private static final String secondConfSecondPaper = "/api/conferences/2/papers/2";
    private static final String secondConfSecondPaperReviews = "/api/conferences/2/papers/2/reviews";
    private static final String secondConfPapers = "/api/conferences/2/papers";

    private static String firstPaperJson;
    private static String listReviews;
    private static String secondPaperJson;
    private static String reviewJson;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        PaperDTO secondPaper = new PaperDTO();
        secondPaper.setId(2);
        secondPaper.setName("The Ironies of Liberation");
        secondPaper.setStatus(Status.accepted);
        secondPaperJson = objectMapper.writeValueAsString(secondPaper);

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setGrade(Grade.weakAccept);
        reviewDTO.setSuggestion("Fix grammar errors");
        reviewJson = objectMapper.writeValueAsString(reviewDTO);

        PaperDTO firstPaper = new PaperDTO();
        firstPaper.setId(1);
        firstPaper.setStatus(Status.accepted);
        firstPaperJson = objectMapper.writeValueAsString(firstPaper);

        UserDTO amyUser = new UserDTO();
        amyUser.setUsername("Amy");
        ReviewDTO thirdReview = new ReviewDTO();
        thirdReview.setId(3);
        thirdReview.setGrade(Grade.weakAccept);
        thirdReview.setSuggestion("Fix grammar errors");
        thirdReview.setReviewer(amyUser);

        ReviewDTO firstReview = new ReviewDTO();
        firstReview.setGrade(Grade.strongAccept);

        ReviewDTO secondReview = new ReviewDTO();
        secondReview.setGrade(Grade.borderlinePaper);

        listReviews = objectMapper.writeValueAsString(new ArrayList<>(List.of(firstReview, secondReview, thirdReview)));
    }

    @Test
    public void acceptOrDeclinePaperExceptions() throws Exception {
        // chair accepts his own paper case
        final String token = LogInUtils.getTokenForLogin(ownPaperReviewerUsername, ownPaperReviewerPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performPutRequest(data, secondConfSecondPaperAccepted, status().is5xxServerError());

        // status already changed case
        performPutRequest(data, secondConfFirstPaperAccepted, status().isOk());
        performPutRequest(data, secondConfFirstPaperAccepted, status().is5xxServerError());
    }

    @Test
    public void acceptOrDeclinePaperTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performPutRequest(data, secondConfFirstPaperAccepted, status().isOk());

        data = RequestData.builder().token(token).resultContent(firstPaperJson).build();
        performGetRequest(data, secondConfFirstPaper, status().isOk());
    }

    @Test
    public void addReviewExceptionsTest() throws Exception {
        // non reviewer case
        final String token = LogInUtils.getTokenForLogin(nonReviewerUsername, nonReviewerPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(reviewJson).build();
        performPostRequest(data, secondConfSecondPaperReviews, status().isForbidden());

        // review own paper case
        final String token2 = LogInUtils.getTokenForLogin(ownPaperReviewerUsername, ownPaperReviewerPassword, getMockMvc());
        data.setToken(token2);
        performPostRequest(data, secondConfSecondPaperReviews, status().is5xxServerError());
    }

    @Test
    public void addReviewTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(pcMemberUsername, pcMemberPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(reviewJson).build();
        performPostRequest(data, secondConfSecondPaperReviews, status().isOk());

        data = RequestData.builder().token(token).resultContent(listReviews).build();
        performGetRequest(data, secondConfSecondPaperReviews, status().isOk());
    }

    @Test
    public void analysePapersTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performPutRequest(data, secondConfPapers, status().isOk());

        data = RequestData.builder().token(token).resultContent(secondPaperJson).build();
        performGetRequest(data, secondConfSecondPaper, status().isOk());
    }

}
