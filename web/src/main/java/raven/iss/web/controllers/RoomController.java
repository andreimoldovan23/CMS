package raven.iss.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import raven.iss.data.api.dtos.RoomDTO;
import raven.iss.data.services.interfaces.RoomService;
import raven.iss.web.security.permissions.AdminChairPermission;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class RoomController {

    private final RoomService roomService;

    @AdminChairPermission
    @GetMapping("/api/conferences/{cid}/rooms")
    public List<RoomDTO> getAllRooms(@PathVariable Integer cid) {
        return roomService.findAll(cid);
    }

    @AdminChairPermission
    @GetMapping("/api/conferences/{cid}/rooms/{id2}")
    public RoomDTO getRoom(@PathVariable Integer cid, @PathVariable Integer id2) {
        return this.roomService.findOne(id2);
    }

    @AdminChairPermission
    @PostMapping("/api/conferences/{cid}/rooms")
    public void addRoom(@PathVariable Integer cid, @Validated @RequestBody RoomDTO roomDTO) {
        this.roomService.add(roomDTO, cid);
    }

    @AdminChairPermission
    @DeleteMapping("/api/conferences/{cid}/rooms/{id2}")
    public void deleteRoom(@PathVariable Integer cid, @PathVariable Integer id2) {
        this.roomService.delete(cid, id2);
    }

    @AdminChairPermission
    @PutMapping("/api/conferences/{cid}/rooms/{id2}")
    public void updateRoom(@PathVariable Integer cid, @PathVariable Integer id2,
                           @Validated @RequestBody RoomDTO roomDTO) {
        this.roomService.update(roomDTO, id2);
    }

}
