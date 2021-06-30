package raven.iss.data.entitiesAndDtos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ChairDTO;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.api.dtos.RoomDTO;
import raven.iss.data.api.dtos.SessionDTO;
import raven.iss.data.api.mappers.*;
import raven.iss.data.model.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SessionMapperTest {
    private final ChairMapper chairMapper = ChairMapper.INSTANCE;
    private final ConferenceMapper conferenceMapper = ConferenceMapper.INSTANCE;
    private final RoomMapper roomMapper = RoomMapper.INSTANCE;
    private final SessionMapper sessionMapper = SessionMapper.INSTANCE;
    private Session session;
    private SessionDTO sessionDTO;

    private static final Integer id = 10;
    private static final String name = "sessionName";
    private static final String topic = "sessionTopic";

    private final Conference conf = Conference.builder()
            .name("ConfName")
            .city("CityName")
            .build();

    private final Room room = Room.builder()
            .name("roomName")
            .capacity(100)
            .conf(conf)
            .build();

    private final Author author = Author.builder().build();

    private final Chair chair = Chair.builder().build();

    private final ChairDTO chairDTO = chairMapper.chairToDTO(chair);
    private final RoomDTO roomDTO = roomMapper.roomToDTO(room);
    private final ConferenceDTO conferenceDTO = conferenceMapper.conferenceToDTO(conf);

    @BeforeEach
    public void setUp() {
        session = Session.builder()
                .name(name)
                .topic(topic)
                .room(room)
                .conf(conf)
                .chair(chair)
                .build();
        session.setId(id);
        session.getSpeakers().add(author);
        sessionDTO = sessionMapper.sessionToDTO(session);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void sessionToSessionDTO() {
        sessionDTO = sessionMapper.sessionToDTO(session);
        assertEquals(sessionDTO.getId(), id);
        assertEquals(sessionDTO.getName(), name);
        assertEquals(sessionDTO.getTopic(), topic);
        assertEquals(sessionDTO.getRoom(), roomDTO);
        assertEquals(sessionDTO.getConf(), conferenceDTO);
        assertEquals(sessionDTO.getChair(), chairDTO);
    }

    @Test
    public void sessionDTOtoSession() {
        session = sessionMapper.DTOtoSession(sessionDTO);
        assertEquals(session.getId(), id);
        assertEquals(session.getName(), name);
        assertEquals(session.getTopic(), topic);
        assertEquals(session.getRoom(), room);
        assertEquals(session.getConf(), conf);
        assertEquals(session.getChair(), chair);
    }

}
