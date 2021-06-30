package raven.iss.data.entitiesAndDtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ChairDTO;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.api.mappers.ChairMapper;
import raven.iss.data.model.Chair;
import raven.iss.data.model.Conference;
import raven.iss.data.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChairMapperTest {

    private static final String confName = "QuantumComputing";
    private static final String confCity = "Los Angeles";
    private static final Integer confId = 30;
    private static final Integer userId = 23;
    private static final String username = "George23";
    private static final String userMail = "george23@gmail.com";
    private static final String userPhoneNumber = "0702040807";
    private static final String userJob = "Janitor";
    private static final String userName = "George";
    private Chair chair;
    private ChairMapper chairMapper;
    private User user;
    private Conference conf;

    @BeforeEach
    public void setUp() {
        chairMapper = ChairMapper.INSTANCE;

        user = User.builder()
                .username(username)
                .mail(userMail)
                .phoneNumber(userPhoneNumber)
                .job(userJob)
                .name(userName)
                .build();
        user.setId(userId);

        conf = Conference.builder()
                .name(confName)
                .city(confCity)
                .build();
        conf.setId(confId);

        chair = Chair.builder()
                .user(user)
                .conf(conf)
                .build();
    }

    @Test
    public void testMapping() {
        ChairDTO chairDTO = chairMapper.chairToDTO(chair);
        UserDTO userDTO = chairDTO.getUser();
        ConferenceDTO conferenceDTO = chairDTO.getConf();

        assertEquals(username, userDTO.getUsername());
        assertEquals(userMail, userDTO.getMail());
        assertEquals(userJob, userDTO.getJob());
        assertEquals(userPhoneNumber, userDTO.getPhoneNumber());
        assertEquals(userName, userDTO.getName());
        assertEquals(userId, userDTO.getId());

        assertEquals(confId, conferenceDTO.getId());
        assertEquals(confName, conferenceDTO.getName());
        assertEquals(confCity, conferenceDTO.getCity());

        chair = chairMapper.DTOtoChair(chairDTO);
        user = chair.getUser();
        conf = chair.getConf();

        assertEquals(username, user.getUsername());
        assertEquals(userMail, user.getMail());
        assertEquals(userJob, user.getJob());
        assertEquals(userPhoneNumber, user.getPhoneNumber());
        assertEquals(userName, user.getName());
        assertEquals(userId, user.getId());

        assertEquals(confId, conf.getId());
        assertEquals(confName, conf.getName());
        assertEquals(confCity, conf.getCity());
    }

}
