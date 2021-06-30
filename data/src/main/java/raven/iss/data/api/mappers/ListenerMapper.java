package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.ListenerDTO;
import raven.iss.data.model.Listener;

@Mapper(config = NonBuilderConfigMapper.class)
public interface ListenerMapper extends DateMapper {

    ListenerMapper INSTANCE = Mappers.getMapper(ListenerMapper.class);

    @Mapping(target = "attendingSections", ignore = true)
    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "stringToDate")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "stringToDate")
    Listener DTOtoListener(ListenerDTO listenerDTO);

    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "dateToString")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "dateToString")
    ListenerDTO listenerToDTO(Listener listener);

}
