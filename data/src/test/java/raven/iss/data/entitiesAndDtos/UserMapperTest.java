package raven.iss.data.entitiesAndDtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.api.mappers.UserMapper;
import raven.iss.data.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    private UserMapper mapper;
    private User user;

    private static final Integer id = 23;
    private static final String username = "George23";
    private static final String mail = "george23@gmail.com";
    private static final String phoneNumber = "0702040807";
    private static final String job = "Janitor";
    private static final String name = "George";
    private static final String website = "http://www.mysite.com";

    @BeforeEach
    public void setUp() {
        mapper = UserMapper.INSTANCE;
        user = User.builder()
                .username(username)
                .mail(mail)
                .phoneNumber(phoneNumber)
                .job(job)
                .name(name)
                .website(website)
                .build();
        user.setId(id);
    }

    @Test
    public void testMapping() {
        UserDTO userDTO = mapper.userToDTO(user);
        assertEquals(userDTO.getUsername(), username);
        assertEquals(userDTO.getMail(), mail);
        assertEquals(userDTO.getJob(), job);
        assertEquals(userDTO.getPhoneNumber(), phoneNumber);
        assertEquals(userDTO.getName(), name);
        assertEquals(userDTO.getId(), id);
        assertEquals(userDTO.getWebsite(), website);

        user = mapper.DTOtoUser(userDTO);
        assertEquals(user.getUsername(), username);
        assertEquals(user.getMail(), mail);
        assertEquals(user.getJob(), job);
        assertEquals(user.getPhoneNumber(), phoneNumber);
        assertEquals(user.getName(), name);
        assertEquals(user.getId(), id);
        assertEquals(user.getWebsite(), website);
    }

}
