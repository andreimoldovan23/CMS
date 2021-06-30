package raven.iss.data.entitiesAndDtos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.PaperDTO;
import raven.iss.data.api.dtos.ReviewDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.api.mappers.ReviewMapper;
import raven.iss.data.model.Grade;
import raven.iss.data.model.Paper;
import raven.iss.data.model.Review;
import raven.iss.data.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewMapperTest {

    private ReviewMapper reviewMapper;
    private Review review;
    private ReviewDTO reviewDTO;

    private static final Integer id = 10;
    private static final Grade grade = Grade.accept;
    private final User user = User.builder()
            .username("userName")
            .name("name")
            .job("job")
            .mail("mail")
            .phoneNumber("0756786877")
            .build();
    private final Paper paper = Paper.builder()
            .name("paperName")
            .build();

    @BeforeEach
    public void setUp() {
        reviewMapper = ReviewMapper.INSTANCE;
        review = Review.builder()
                .grade(grade)
                .reviewer(user)
                .paper(paper)
                .build();
        review.setId(id);
        reviewDTO = reviewMapper.reviewToDTO(review);
    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void reviewToReviewDTO() {
        reviewDTO = reviewMapper.reviewToDTO(review);
        UserDTO userDTO = reviewDTO.getReviewer();
        PaperDTO paperDTO = reviewDTO.getPaper();

        assertEquals(reviewDTO.getId(), id);
        assertEquals(reviewDTO.getGrade(), grade);

        assertEquals(userDTO.getId(), user.getId());
        assertEquals(userDTO.getUsername(), user.getUsername());
        assertEquals(userDTO.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(userDTO.getName(), user.getName());
        assertEquals(userDTO.getJob(), user.getJob());
        assertEquals(userDTO.getMail(), user.getMail());

        assertEquals(paperDTO.getId(), paper.getId());
        assertEquals(paperDTO.getName(), paper.getName());
    }

    @Test
    public void reviewDTOtoReview() {
        review = reviewMapper.DTOtoReview(reviewDTO);
        User revUser = review.getReviewer();
        Paper revPaper = review.getPaper();

        assertEquals(review.getId(), id);
        assertEquals(review.getGrade(), grade);

        assertEquals(revUser.getId(), user.getId());
        assertEquals(revUser.getUsername(), user.getUsername());
        assertEquals(revUser.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(revUser.getName(), user.getName());
        assertEquals(revUser.getJob(), user.getJob());
        assertEquals(revUser.getMail(), user.getMail());

        assertEquals(revPaper.getId(), paper.getId());
        assertEquals(revPaper.getName(), paper.getName());
    }

}