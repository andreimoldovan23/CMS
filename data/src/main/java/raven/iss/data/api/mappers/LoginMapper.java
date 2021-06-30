package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.LoginDTO;
import raven.iss.data.model.User;

@Mapper(config = NonBuilderConfigMapper.class)
public interface LoginMapper {

    LoginMapper INSTANCE = Mappers.getMapper(LoginMapper.class);

    @Mapping(target = "website", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User DTOtoUser(LoginDTO dto);

    @Mapping(target = "password", ignore = true)
    LoginDTO userToDTO(User user);

}
