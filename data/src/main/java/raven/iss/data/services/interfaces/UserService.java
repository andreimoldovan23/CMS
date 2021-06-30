package raven.iss.data.services.interfaces;

import raven.iss.data.api.dtos.LoginDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.model.User;

import java.util.List;

public interface UserService {

    UserDTO findDTObyUsername(String username);
    User findByUsername(String username);
    void signUp(LoginDTO userDTO);
    void registerAuthor(String username, Integer confId);
    void registerListener(String username, Integer confId);

    List<UserDTO> getAll();
}
