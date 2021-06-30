package raven.iss.data.entitiesAndDtos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.api.dtos.RoomDTO;
import raven.iss.data.api.mappers.RoomMapper;
import raven.iss.data.model.Conference;
import raven.iss.data.model.Room;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomMapperTest {
    private RoomMapper roomMapper;
    private Room room;
    private RoomDTO roomDTO;

    private static final Integer id = 10;
    private static final String name = "roomName";
    private static final Integer capacity = 200;
    private final Conference conf = Conference.builder()
            .name("ConfName")
            .city("CityName")
            .build();

    @BeforeEach
    public void setUp() {
        roomMapper = RoomMapper.INSTANCE;
        room = Room.builder()
                .name(name)
                .capacity(capacity)
                .conf(conf)
                .build();
        room.setId(id);
        roomDTO = roomMapper.roomToDTO(room);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void roomToRoomDTO() {
        roomDTO = roomMapper.roomToDTO(room);
        ConferenceDTO conferenceDTO = roomDTO.getConf();

        assertEquals(roomDTO.getId(), id);
        assertEquals(roomDTO.getName(), name);
        assertEquals(roomDTO.getCapacity(), capacity);

        assertEquals(conferenceDTO.getName(), conf.getName());
        assertEquals(conferenceDTO.getCity(), conf.getCity());
        assertEquals(conferenceDTO.getId(), conf.getId());
    }

    @Test
    public void roomDTOtoRoom() {
        room = roomMapper.DTOtoRoom(roomDTO);
        Conference conference = room.getConf();

        assertEquals(room.getId(), id);
        assertEquals(room.getName(), name);
        assertEquals(room.getCapacity(), capacity);

        assertEquals(conference.getName(), conf.getName());
        assertEquals(conference.getCity(), conf.getCity());
        assertEquals(conference.getId(), conf.getId());
    }

}