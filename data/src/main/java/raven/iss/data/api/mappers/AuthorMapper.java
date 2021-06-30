package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.AuthorDTO;
import raven.iss.data.model.Author;

@Mapper(config = NonBuilderConfigMapper.class)
public interface AuthorMapper extends DateMapper {

    AuthorMapper INSTANCE = Mappers.getMapper(AuthorMapper.class);

    @Mapping(target = "sessionSpeakers", ignore = true)
    @Mapping(target = "papers", ignore = true)
    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "stringToDate")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "stringToDate")
    Author DTOtoAuthor(AuthorDTO authorDTO);

    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "dateToString")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "dateToString")
    AuthorDTO authorToDTO(Author author);

}
