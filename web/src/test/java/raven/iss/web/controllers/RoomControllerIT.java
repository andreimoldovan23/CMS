package raven.iss.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.api.dtos.RoomDTO;
import raven.iss.data.api.mappers.ConferenceMapper;
import raven.iss.data.api.mappers.RoomMapper;
import raven.iss.data.model.Conference;
import raven.iss.data.model.Room;
import raven.iss.web.controllers.Utils.AbstractRestControllerTest;
import raven.iss.web.controllers.Utils.LogInUtils;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoomControllerIT extends AbstractRestControllerTest {
    private static final String adminUsername = "Root";
    private static final String adminPassword = "root_Admin_2#";
    private static final String secondConfSecondRoom = "/api/conferences/2/rooms/2";
    private static final String secondConfFirstRoom = "/api/conferences/2/rooms/1";
    private static final String secondConfFifthRoom = "/api/conferences/2/rooms/5";
    private static final String secondConfRooms = "/api/conferences/2/rooms";

    private static String secondRoom;
    private static String fifthRoom;
    private static String firstRoom;
    private static String firstRoomUpdated;
    private static String addJsonBody;
    private static String updateJsonBody;

    private static final ConferenceMapper conferenceMapper = ConferenceMapper.INSTANCE;
    private static final RoomMapper roomMapper = RoomMapper.INSTANCE;

    @BeforeAll
    public static void setUp() throws JsonProcessingException {
        ConferenceDTO conferenceDTO = conferenceMapper.conferenceToDTO(Conference.builder()
                .name("AllStar Conference")
                .startDate(LocalDateTime.of(2021, 8, 2, 2, 2, 2))
                .endDate(LocalDateTime.of(2021, 8, 4, 4, 4, 4))
                .build());

        RoomDTO secondRoomDto = roomMapper.roomToDTO(Room.builder().name("Europa").capacity(150).build());
        secondRoomDto.setConf(conferenceDTO);
        secondRoomDto.setId(2);
        secondRoom = objectMapper.writeValueAsString(secondRoomDto);

        RoomDTO fifthRoomDto = roomMapper.roomToDTO(Room.builder().name("newroom").capacity(100).build());
        fifthRoomDto.setConf(conferenceDTO);
        fifthRoomDto.setId(5);
        fifthRoom = objectMapper.writeValueAsString(fifthRoomDto);

        RoomDTO firstRoomDto = roomMapper.roomToDTO(Room.builder().name("Plato").capacity(200).build());
        firstRoomDto.setId(1);
        firstRoom = objectMapper.writeValueAsString(firstRoomDto);

        firstRoomDto.setName("updatedRoom");
        firstRoomDto.setConf(conferenceDTO);
        firstRoomDto.setCapacity(100);
        firstRoomUpdated = objectMapper.writeValueAsString(firstRoomDto);

        RoomDTO addRoom = roomMapper.roomToDTO(Room.builder().name("newroom").capacity(100).build());
        RoomDTO updateRoom = roomMapper.roomToDTO(Room.builder().name("updatedRoom").capacity(100).build());
        addJsonBody = objectMapper.writeValueAsString(addRoom);
        updateJsonBody = objectMapper.writeValueAsString(updateRoom);
    }

    @Test
    public void getRoomTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(secondRoom).build();
        performGetRequest(data, secondConfSecondRoom, status().isOk());
    }

    @Test
    public void addRoomTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(addJsonBody).build();
        performPostRequest(data, secondConfRooms, status().isOk());

        data = RequestData.builder().token(token).resultContent(fifthRoom).build();
        performGetRequest(data, secondConfFifthRoom, status().isOk());

        data = RequestData.builder().token(token).content(addJsonBody).build();
        performPostRequest(data, secondConfRooms, status().is5xxServerError());
    }

    @Test
    public void deleteRoomTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).resultContent(firstRoom).build();
        performGetRequest(data, secondConfFirstRoom, status().isOk());

        data = RequestData.builder().token(token).build();
        performDeleteRequest(data, secondConfFirstRoom, status().isOk());

        performGetRequest(data, secondConfFirstRoom, status().is4xxClientError());
    }

    @Test
    public void updateRoomTest() throws Exception {
        final String token = LogInUtils.getTokenForLogin(adminUsername, adminPassword, getMockMvc());
        RequestData data = RequestData.builder().token(token).content(updateJsonBody).build();
        performPutRequest(data, secondConfFirstRoom, status().isOk());

        data = RequestData.builder().token(token).resultContent(firstRoomUpdated).build();
        performGetRequest(data, secondConfFirstRoom, status().isOk());
    }

}
