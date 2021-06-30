package raven.iss.data.services.interfaces;

import raven.iss.data.api.dtos.RoomDTO;

import java.util.List;

public interface RoomService {
    void add(RoomDTO room, Integer confId);
    void delete(Integer cid, Integer id);
    void update(RoomDTO room, Integer roomId);
    List<RoomDTO> findAll(Integer id);
    RoomDTO findOne(Integer id2);
}
