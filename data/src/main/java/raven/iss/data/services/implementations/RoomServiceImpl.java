package raven.iss.data.services.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.api.dtos.RoomDTO;
import raven.iss.data.api.mappers.RoomMapper;
import raven.iss.data.constants.Messages;
import raven.iss.data.exceptions.InternalErrorException;
import raven.iss.data.model.Conference;
import raven.iss.data.model.Room;
import raven.iss.data.repositories.conferenceFragments.ConferenceRepo;
import raven.iss.data.repositories.RoomRepo;
import raven.iss.data.services.interfaces.RoomService;
import raven.iss.data.validators.Helper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;
    private final ConferenceRepo conferenceRepo;
    private final RoomMapper roomMapper;

    private void isRoomWithSameName(Conference conference, Room room) {
        conference.getRooms().stream()
                .filter(room1 -> room1.getName().equals(room.getName()))
                .findFirst().ifPresent(room1 -> {
                    throw new InternalErrorException(Messages.roomAtConfAlreadyExists);
        });
    }

    private Room getRoomFromConf(Conference conference, Integer id) {
        return (Room) Helper.checkNull(conference.getRooms().stream()
                .filter(room -> room.getId().equals(id))
                .findFirst().orElse(null), Messages.roomNotFound);
    }

    @Transactional
    @Override
    public void add(RoomDTO room1, Integer confId) {
        log.trace("Room service - add - cid {}, data {}", confId, room1.toString());
        Room room = roomMapper.DTOtoRoom(room1);
        log.trace("Room service - add - fetch conference");
        Conference conf = (Conference) Helper.checkNull(conferenceRepo.findByIdWithRooms(confId), Messages.conferenceNotFound);
        log.trace("Room service - add - check if already room with same name");
        isRoomWithSameName(conf, room);
        room.setConf(conf);
        conf.getRooms().add(room);
        log.trace("Room service - add - saving data...");
        conferenceRepo.save(conf);
    }

    @Transactional
    @Override
    public void delete(Integer cid, Integer id) {
        log.trace("Room service - delete - cid {}, id {}", cid, id);
        Conference conf = (Conference) Helper.checkNull(conferenceRepo.findByIdWithRooms(cid), Messages.conferenceNotFound);
        log.trace("Room service - delete - check if room exists");
        Room room = getRoomFromConf(conf, id);
        conf.getRooms().remove(room);
        log.trace("Room service - delete - saving data...");
        conferenceRepo.save(conf);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void update(RoomDTO room1, Integer roomID) {
        log.trace("Room service - update - rid {}, data {}", roomID, room1.toString());
        Room room = (Room) Helper.checkNull(roomRepo.findById(roomID).orElse(null), Messages.roomNotFound);
        Room toUpdate = roomMapper.DTOtoRoom(room1);
        room.setName(toUpdate.getName() != null ? toUpdate.getName() : room.getName());
        room.setCapacity(toUpdate.getCapacity() != null ? toUpdate.getCapacity() : room.getCapacity());
        log.trace("Room service - update - saving data...");
        roomRepo.save(room);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RoomDTO> findAll(Integer id) {
        log.trace("Room service - findAll - cid {}", id);
        return roomRepo.findRoomsByConfId(id).stream()
                .map(roomMapper::roomToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public RoomDTO findOne(Integer idRoom) {
        log.trace("Room service - find one - rid {}", idRoom);
        return roomMapper.roomToDTO((Room) Helper.checkNull(roomRepo.findById(idRoom).orElse(null),
                Messages.roomNotFound));
    }

}
