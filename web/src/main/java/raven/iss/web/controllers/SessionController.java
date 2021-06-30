package raven.iss.web.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import raven.iss.data.api.dtos.AuthorDTO;
import raven.iss.data.api.dtos.SessionDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.exceptions.NotFoundException;
import raven.iss.data.services.interfaces.SessionService;
import raven.iss.web.security.jwt.SecurityUtils;
import raven.iss.web.security.permissions.ALAPCPermission;
import raven.iss.web.security.permissions.AdminChairPermission;
import raven.iss.web.security.permissions.ListenerPermission;

import java.util.List;

import static raven.iss.web.controllers.UtilsHelper.getUsername;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SessionController {

    private final SessionService sessionService;

    @ALAPCPermission
    @GetMapping("/api/conferences/{cid}/sessions")
    public List<SessionDTO> getAll(@PathVariable Integer cid) {
        return sessionService.getAll(cid);
    }

    @AdminChairPermission
    @PostMapping("/api/conferences/{cid}/sessions")
    public SessionDTO addSession(@PathVariable Integer cid, @Validated @RequestBody SessionDTO sessionDTO) {
        return sessionService.addSession(cid, sessionDTO);
    }

    @ALAPCPermission
    @GetMapping("/api/conferences/{cid}/sessions/{id}")
    public SessionDTO getSession(@PathVariable Integer cid, @PathVariable Integer id) {
        return sessionService.getSession(id);
    }

    @AdminChairPermission
    @PutMapping("/api/conferences/{cid}/sessions/{id}")
    public void updateSession(@PathVariable Integer cid, @PathVariable Integer id,
                              @Validated @RequestBody SessionDTO sessionDTO) {
        sessionService.updateSession(id, sessionDTO);
    }

    @AdminChairPermission
    @DeleteMapping("/api/conferences/{cid}/sessions/{id}")
    public void deleteSession(@PathVariable Integer cid, @PathVariable Integer id) {
        sessionService.deleteSession(cid, id);
    }

    @ListenerPermission
    @PutMapping("/api/conferences/{cid}/sessions/{id}/register")
    public void attendSession(@PathVariable Integer cid, @PathVariable Integer id) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow(() -> {
            throw new NotFoundException("No such user");
        });
        sessionService.attendSession(cid, id, username);
    }

    @ListenerPermission
    @GetMapping("/api/conferences/{cid}/sessions/{sid}/isAttending")
    public AttendsData isAttendingSessions(@PathVariable Integer cid, @PathVariable Integer sid) {
        return new AttendsData(this.sessionService.isListenerAttending(cid, sid, getUsername()));
    }

    @AdminChairPermission
    @GetMapping("/api/conferences/{cid}/sessions/{id}/speakers")
    public List<UserDTO> getAllSpeakers(@PathVariable Integer cid, @PathVariable Integer id) {
        return sessionService.getAllSpeakers(cid, id);
    }

    @AdminChairPermission
    @PostMapping("/api/conferences/{cid}/sessions/{id}/speakers")
    public void addSpeaker(@PathVariable Integer cid, @PathVariable Integer id, @Validated @RequestBody AuthorDTO userDTO) {
        sessionService.addSpeakerToSession(cid, id, userDTO);
    }

    @AdminChairPermission
    @DeleteMapping("/api/conferences/{cid}/sessions/{id}/speakers")
    public void deleteSpeaker(@PathVariable Integer cid, @PathVariable Integer id,
                              @RequestParam(name = "username") String username) {
        sessionService.deleteSpeakerFromSession(cid, id, username);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AttendsData {
        boolean isAttending;
    }

}
