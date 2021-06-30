package raven.iss.data.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import raven.iss.data.api.dtos.RoomDTO;
import raven.iss.data.model.Room;

@Mapper(config = NonBuilderConfigMapper.class)
public interface RoomMapper extends DateMapper {

    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "dateToString")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "dateToString")
    RoomDTO roomToDTO(Room room);

    @Mapping(source = "conf.startDate", target = "conf.startDate", qualifiedByName = "stringToDate")
    @Mapping(source = "conf.endDate", target = "conf.endDate", qualifiedByName = "stringToDate")
    Room DTOtoRoom(RoomDTO roomDTO);

}
