package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.model.Conference;

@Mapper(config = NonBuilderConfigMapper.class)
public interface ConferenceMapper extends DateMapper {

    ConferenceMapper INSTANCE = Mappers.getMapper(ConferenceMapper.class);

    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "stringToDate")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "stringToDate")
    Conference DTOtoConference(ConferenceDTO conferenceDTO);

    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "dateToString")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "dateToString")
    ConferenceDTO conferenceToDTO(Conference conference);

}
