package raven.iss.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import raven.iss.data.api.dtos.*;
import raven.iss.data.services.interfaces.ConferenceService;
import raven.iss.web.security.permissions.*;

import java.util.List;

import static raven.iss.web.controllers.UtilsHelper.getUsername;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ConferenceController {

    private final ConferenceService conferenceService;

    @GetMapping("/api/conferences")
    public List<ConferenceDTO> getAll() {
        return conferenceService.getAll();
    }

    @GetMapping("/api/conferences/{cid}")
    public ConferenceDTO getOne(@PathVariable Integer cid) {
        return conferenceService.findDTObyId(cid);
    }

    @AdminPermission
    @PostMapping("/api/conferences")
    public ConferenceDTO add(@Validated @RequestBody ConferenceDTO dto) {
        return conferenceService.add(dto);
    }

    @AdminPermission
    @PutMapping("/api/conferences/{cid}")
    public void update(@PathVariable Integer cid, @Validated @RequestBody ConferenceDTO dto) {
        conferenceService.edit(cid, dto);
    }

    @AdminPermission
    @DeleteMapping("/api/conferences/{cid}")
    public void delete(@PathVariable Integer cid) {
        conferenceService.deleteById(cid);
    }

    @AdminPermission
    @PostMapping("/api/conferences/{cid}/chairs")
    public void addChair(@PathVariable Integer cid, @Validated @RequestBody ChairDTO dto) {
        conferenceService.addChair(cid, dto);
    }

    @AdminChairPermission
    @GetMapping("/api/conferences/{cid}/chairs")
    public List<ChairDTO> getAllChairs(@PathVariable Integer cid) {
        return conferenceService.getAllChairs(cid);
    }

    @AdminPermission
    @DeleteMapping("/api/conferences/{cid}/chairs")
    public void deleteChair(@PathVariable Integer cid, @RequestParam(name = "username") String username) {
        conferenceService.deleteChair(cid, username);
    }

    @AdminPermission
    @GetMapping("/api/conferences/{cid}/filterNotChair")
    public List<UserDTO> filterUsersNotChair(@PathVariable Integer cid) {
        return conferenceService.filterNotChair(cid, getUsername());
    }

    @ChairPermission
    @GetMapping("/api/conferences/{cid}/filterNotPCMember")
    public List<UserDTO> filterUsersNotPCMember(@PathVariable Integer cid) {
        return conferenceService.filterNotPcMember(cid, getUsername());
    }

    @AdminChairPermission
    @GetMapping("/api/conferences/{cid}/authors")
    public List<AuthorDTO> filterAuthorsWithAcceptedPapers(@PathVariable Integer cid) {
        return conferenceService.filterAuthorsWithAcceptedPapers(cid);
    }

    @IsAuthorPermission
    @GetMapping("/api/conferences/{cid}/allAuthors")
    public List<AuthorDTO> filterAuthors(@PathVariable Integer cid) {
        return conferenceService.getAllAuthors(cid);
    }

    @ChairPermission
    @GetMapping("/api/conferences/{cid}/pcmembers")
    public List<PCMemberDTO> getAllPcMembers(@PathVariable Integer cid) {
        return conferenceService.getAllPcMembers(cid);
    }

    @ChairPermission
    @PostMapping("/api/conferences/{cid}/pcmembers")
    public void addPcMember(@PathVariable Integer cid, @Validated @RequestBody PCMemberDTO dto) {
        conferenceService.addPcMember(cid, dto, getUsername());
    }

    @PCMemberPermission
    @PutMapping("/api/conferences/{cid}/pcmembers/bidding")
    public void bid(@PathVariable Integer cid, @RequestParam("available") Boolean likesToReview) {
        conferenceService.bid(cid, getUsername(), likesToReview);
    }

    @ChairPermission
    @PostMapping("/api/conferences/{cid}/papers/{pid}/reviewers")
    public void addReviewerToPaper(@PathVariable Integer cid, @PathVariable Integer pid,
                                   @Validated @RequestBody PCMemberDTO pcMemberDTO) {
        conferenceService.addReviewerToPaper(cid, pid, pcMemberDTO);
    }

    @ChairPermission
    @GetMapping("/api/conferences/{cid}/papers/{pid}/pcmembersNotAssigned")
    public List<PCMemberDTO> getPcMembersNotAssignedSpecificPaper(@PathVariable Integer cid, @PathVariable Integer pid) {
        return conferenceService.getPcMembersNotAssignedSpecificPaper(cid, pid);
    }

    @PCMemberPermission
    @GetMapping("/api/conferences/{cid}/pcmembers/hasBidded")
    public PCMemberDTO getIfPcMemberBidded(@PathVariable Integer cid) {
        return conferenceService.getPcMemberBidded(cid, getUsername());
    }

}
