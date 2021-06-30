package raven.iss.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.api.dtos.PhaseDTO;
import raven.iss.data.api.mappers.ConferenceMapper;
import raven.iss.data.api.mappers.PhaseMapper;
import raven.iss.data.model.Conference;
import raven.iss.data.model.Phase;
import raven.iss.web.controllers.Utils.AbstractRestControllerTest;
import raven.iss.web.controllers.Utils.LogInUtils;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PhaseControllerIT extends AbstractRestControllerTest {

    private static final String adminUsername = "Root";
    private static final String adminPassword = "root_Admin_2#";
    private static final String chairUsername = "Greg";
    private static final String chairPassword = "Greg_2#?";
    private static final String secondConfPhases = "/api/conferences/2/phases";
    private static final String secondConf9thPhase = "/api/conferences/2/phases/9";
    private static final String secondConfFourthPhase = "/api/conferences/2/phases/4";
    private static final String secondConfFourthPhaseStart = "/api/conferences/2/phases/4/start";

    private static String phaseJson;
    private static String invalidPhaseJson;
    private static String invalidDatePhaseJson;
    private static String addedPhaseJson;
    private static String fourthPhaseJson;
    private static String updatedFourthPhaseJson;
    private static String startedFourthPhaseJson;

    private static final PhaseMapper phaseMapper = PhaseMapper.INSTANCE;
    private static final ConferenceMapper conferenceMapper = ConferenceMapper.INSTANCE;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        PhaseDTO toAddPhaseDto = phaseMapper.phaseToDTO(Phase.builder()
                .name("SESSION_REGISTRATION")
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .build());
        phaseJson = objectMapper.writeValueAsString(toAddPhaseDto);

        PhaseDTO invalidPhase = phaseMapper.phaseToDTO(Phase.builder()
                .name("test")
                .deadline(LocalDateTime.of(2021, 7, 10, 12, 0, 0))
                .build());
        invalidPhaseJson = objectMapper.writeValueAsString(invalidPhase);

        PhaseDTO invalidPhaseDates = phaseMapper.phaseToDTO(Phase.builder()
                .name("SESSION_REGISTRATION")
                .deadline(LocalDateTime.of(2021, 10, 10, 2, 2, 2))
                .build());
        invalidDatePhaseJson = objectMapper.writeValueAsString(invalidPhaseDates);

        ConferenceDTO conferenceDTO = conferenceMapper.conferenceToDTO(Conference.builder()
                .name("AllStar Conference")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build());

        toAddPhaseDto.setConf(conferenceDTO);
        toAddPhaseDto.setId(9);
        addedPhaseJson = objectMapper.writeValueAsString(toAddPhaseDto);

        toAddPhaseDto.setId(4);
        updatedFourthPhaseJson = objectMapper.writeValueAsString(toAddPhaseDto);

        toAddPhaseDto.setName("REGISTRATION");
        fourthPhaseJson = objectMapper.writeValueAsString(toAddPhaseDto);

        toAddPhaseDto.setIsActive(true);
        startedFourthPhaseJson = objectMapper.writeValueAsString(toAddPhaseDto);
    }

    @Test
    public void addPhase() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(phaseJson).build();
        performPostRequest(data, secondConfPhases, status().isOk());

        data = RequestData.builder().token(token).resultContent(addedPhaseJson).build();
        performGetRequest(data, secondConf9thPhase, status().isOk());
    }

    @Test
    public void addInvalidPhase() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(invalidPhaseJson).build();
        performPostRequest(data, secondConfPhases, status().is4xxClientError());

        data = RequestData.builder().token(token).content(invalidDatePhaseJson).build();
        performPostRequest(data, secondConfPhases, status().is5xxServerError());
    }

    @Test
    public void getPhase() throws Exception {
        RequestData data = RequestData.builder().resultContent(fourthPhaseJson).build();
        performGetRequest(data, secondConfFourthPhase, status().isOk());
    }

    @Test
    public void updatePhase() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(phaseJson).build();
        performPutRequest(data, secondConfFourthPhase, status().isOk());

        data = RequestData.builder().token(token).resultContent(updatedFourthPhaseJson).build();
        performGetRequest(data, secondConfFourthPhase, status().isOk());
    }

    @Test
    public void updateInvalidPhase() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(invalidPhaseJson).build();
        performPutRequest(data, secondConfFourthPhase, status().is4xxClientError());

        data = RequestData.builder().token(token).content(invalidDatePhaseJson).build();
        performPutRequest(data, secondConfFourthPhase, status().is5xxServerError());
    }

    @Test
    public void deletePhase() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(fourthPhaseJson).build();
        performGetRequest(data, secondConfFourthPhase, status().isOk());

        data = RequestData.builder().token(token).build();
        performDeleteRequest(data, secondConfFourthPhase, status().isOk());

        performGetRequest(data, secondConfFourthPhase, status().is4xxClientError());
    }

    @Test
    public void startPhase() throws Exception {
        final String token = LogInUtils.getTokenForLogin(chairUsername, chairPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).build();
        performPutRequest(data, secondConfFourthPhaseStart, status().isOk());

        data = RequestData.builder().token(token).resultContent(startedFourthPhaseJson).build();
        performGetRequest(data, secondConfFourthPhase, status().isOk());
    }
}
