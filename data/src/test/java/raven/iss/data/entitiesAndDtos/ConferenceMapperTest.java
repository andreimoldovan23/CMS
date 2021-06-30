package raven.iss.data.entitiesAndDtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.api.mappers.ConferenceMapper;
import raven.iss.data.model.Conference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConferenceMapperTest {

    private static final String name = "QuantumComputing";
    private static final String city = "Los Angeles";
    private static final LocalDateTime date = LocalDateTime.of(2002, 2, 2, 2, 2, 2);
    private static final Integer id = 30;
    private Conference conference;
    private ConferenceMapper conferenceMapper;

    @BeforeEach
    public void setUp() {
        conferenceMapper = ConferenceMapper.INSTANCE;
        conference = Conference.builder()
                .name(name)
                .startDate(date)
                .endDate(date)
                .city(city)
                .build();
        conference.setId(id);
    }

    @Test
    public void testMapping() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd//HH::mm::ss");
        ConferenceDTO conferenceDTO = conferenceMapper.conferenceToDTO(conference);
        assertEquals(id, conferenceDTO.getId());
        assertEquals(name, conferenceDTO.getName());
        assertEquals(date.format(formatter), conferenceDTO.getStartDate());
        assertEquals(city, conferenceDTO.getCity());

        Conference conf = conferenceMapper.DTOtoConference(conferenceDTO);
        assertEquals(id, conf.getId());
        assertEquals(name, conf.getName());
        assertEquals(date, conf.getStartDate());
        assertEquals(date, conf.getEndDate());
        assertEquals(city, conf.getCity());
    }

}
