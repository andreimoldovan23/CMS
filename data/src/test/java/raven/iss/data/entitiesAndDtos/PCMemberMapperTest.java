package raven.iss.data.entitiesAndDtos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.api.dtos.PCMemberDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.api.mappers.ConferenceMapper;
import raven.iss.data.api.mappers.PCMemberMapper;
import raven.iss.data.api.mappers.UserMapper;
import raven.iss.data.model.Conference;
import raven.iss.data.model.PCMember;
import raven.iss.data.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PCMemberMapperTest {
    private PCMemberMapper pcMemberMapper;
    private final ConferenceMapper conferenceMapper = ConferenceMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private PCMember pcMember;
    private PCMemberDTO pcMemberDTO;

    private final Conference conf = Conference.builder()
            .name("ConfName")
            .city("CityName")
            .build();
    private final User user = User.builder()
            .username("userName")
            .name("name")
            .job("job")
            .mail("mail")
            .phoneNumber("0756786877")
            .build();
    private final UserDTO userDTO = userMapper.userToDTO(user);
    private final ConferenceDTO conferenceDTO = conferenceMapper.conferenceToDTO(conf);

    @BeforeEach
    public void setUp() {
        pcMemberMapper = PCMemberMapper.INSTANCE;
        pcMember = PCMember.builder()
                .conf(conf)
                .user(user)
                .build();
        pcMemberDTO = pcMemberMapper.pcMemberToDTO(pcMember);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void pcMemberToPCMemberDTO() {
        pcMemberDTO = pcMemberMapper.pcMemberToDTO(pcMember);
        ConferenceDTO pcConfDto = pcMemberDTO.getConf();
        UserDTO pcUserDTO = pcMemberDTO.getUser();

        assertEquals(pcConfDto.getCity(), conferenceDTO.getCity());
        assertEquals(pcConfDto.getName(), conferenceDTO.getName());
        assertEquals(pcConfDto.getId(), conferenceDTO.getId());

        assertEquals(pcUserDTO.getUsername(), userDTO.getUsername());
        assertEquals(pcUserDTO.getJob(), userDTO.getJob());
        assertEquals(pcUserDTO.getMail(), userDTO.getMail());
        assertEquals(pcUserDTO.getPhoneNumber(), userDTO.getPhoneNumber());
        assertEquals(pcUserDTO.getName(), userDTO.getName());
        assertEquals(pcUserDTO.getId(), userDTO.getId());
    }

    @Test
    public void pcMemberDTOtoPCMember() {
        pcMember = pcMemberMapper.DTOtoPCMember(pcMemberDTO);
        Conference pcConf = pcMember.getConf();
        User pcUser = pcMember.getUser();

        assertEquals(pcConf.getCity(), conf.getCity());
        assertEquals(pcConf.getName(), conf.getName());
        assertEquals(pcConf.getId(), conf.getId());

        assertEquals(pcUser.getUsername(), user.getUsername());
        assertEquals(pcUser.getJob(), user.getJob());
        assertEquals(pcUser.getMail(), user.getMail());
        assertEquals(pcUser.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(pcUser.getName(), user.getName());
        assertEquals(pcUser.getId(), user.getId());
    }

}