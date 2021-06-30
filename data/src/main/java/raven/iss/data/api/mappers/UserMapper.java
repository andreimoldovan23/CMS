package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.model.User;

@Mapper(config = NonBuilderConfigMapper.class)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToDTO(User user);

    @Mapping(target = "profile", ignore = true)
    User DTOtoUser(UserDTO userDTO);

}
