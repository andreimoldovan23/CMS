package raven.iss.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import raven.iss.data.api.dtos.PhaseDTO;
import raven.iss.data.services.interfaces.PhaseService;
import raven.iss.web.security.permissions.AdminPermission;
import raven.iss.web.security.permissions.ChairPermission;
import raven.iss.web.security.permissions.UserPermission;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class PhaseController {

    private final PhaseService phaseService;

    @GetMapping("/api/conferences/{cid}/phases")
    public List<PhaseDTO> getAll(@PathVariable Integer cid) {
        return phaseService.getAll(cid);
    }

    @AdminPermission
    @PostMapping("/api/conferences/{cid}/phases")
    public void addPhase(@PathVariable Integer cid, @Validated @RequestBody PhaseDTO phaseDTO) {
        phaseService.addPhase(cid, phaseDTO);
    }

    @GetMapping("/api/conferences/{cid}/phases/{id}")
    public PhaseDTO getPhase(@PathVariable Integer cid, @PathVariable Integer id) {
        return phaseService.getPhase(id);
    }

    @AdminPermission
    @PutMapping("/api/conferences/{cid}/phases/{id}")
    public void updatePhase(@PathVariable Integer cid, @PathVariable Integer id,
                            @Validated @RequestBody PhaseDTO phaseDTO) {
        phaseService.updatePhase(cid, id, phaseDTO);
    }

    @ChairPermission
    @PutMapping("/api/conferences/{cid}/phases/{id}/start")
    public void startPhase(@PathVariable Integer cid, @PathVariable Integer id) {
        phaseService.startPhase(id);
    }

    @AdminPermission
    @DeleteMapping("/api/conferences/{cid}/phases/{id}")
    public void deletePhase(@PathVariable Integer cid, @PathVariable("id") Integer id) {
        phaseService.deletePhase(cid, id);
    }

}
