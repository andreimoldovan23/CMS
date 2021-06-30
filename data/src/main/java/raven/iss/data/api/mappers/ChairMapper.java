package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.ChairDTO;
import raven.iss.data.model.Chair;

@Mapper(config = NonBuilderConfigMapper.class)
public interface ChairMapper extends DateMapper {

    ChairMapper INSTANCE = Mappers.getMapper(ChairMapper.class);

    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "stringToDate")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "stringToDate")
    Chair DTOtoChair(ChairDTO chairDTO);

    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "dateToString")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "dateToString")
    ChairDTO chairToDTO(Chair chair);

}
