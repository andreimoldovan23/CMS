package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.PCMemberDTO;
import raven.iss.data.model.PCMember;

@Mapper(config = NonBuilderConfigMapper.class)
public interface PCMemberMapper extends DateMapper {

    PCMemberMapper INSTANCE = Mappers.getMapper(PCMemberMapper.class);

    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "dateToString")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "dateToString")
    PCMemberDTO pcMemberToDTO(PCMember pcMember);

    @Mapping(target = "toReview", ignore = true)
    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "stringToDate")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "stringToDate")
    PCMember DTOtoPCMember(PCMemberDTO pcMemberDTO);

}