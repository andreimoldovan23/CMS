package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.SessionDTO;
import raven.iss.data.model.Session;

@Mapper(config = NonBuilderConfigMapper.class)
public interface SessionMapper extends DateMapper {

    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);

    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "dateToString")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "dateToString")
    SessionDTO sessionToDTO(Session session);

    @Mapping(target = "speakers", ignore = true)
    @Mapping(target = "watchers", ignore = true)
    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "stringToDate")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "stringToDate")
    Session DTOtoSession(SessionDTO sessionDTO);

}
