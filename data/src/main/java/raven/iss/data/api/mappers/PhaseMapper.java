package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.PhaseDTO;
import raven.iss.data.model.Phase;

@Mapper(config = NonBuilderConfigMapper.class)
public interface PhaseMapper extends DateMapper {

    PhaseMapper INSTANCE = Mappers.getMapper(PhaseMapper.class);

    @Mapping(source = "deadline", target = "deadlineString", qualifiedByName = "dateToString")
    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "dateToString")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "dateToString")
    PhaseDTO phaseToDTO(Phase phase);

    @Mapping(source = "deadlineString", target = "deadline", qualifiedByName = "stringToDate")
    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "stringToDate")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "stringToDate")
    Phase DTOtoPhase(PhaseDTO phaseDTO);

}