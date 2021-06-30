package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.PaperDTO;
import raven.iss.data.model.Paper;

@Mapper(config = NonBuilderConfigMapper.class)
public interface PaperMapper {

    PaperMapper INSTANCE = Mappers.getMapper(PaperMapper.class);

    PaperDTO paperToDTO(Paper paper);

    @Mapping(target = "presentationFile", ignore = true)
    @Mapping(target = "shouldReview", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "paperFile", ignore = true)
    @Mapping(target = "authors", ignore = true)
    Paper DTOtoPaper(PaperDTO paperDTO);

}