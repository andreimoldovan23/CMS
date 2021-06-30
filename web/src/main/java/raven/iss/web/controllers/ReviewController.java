package raven.iss.web.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import raven.iss.data.api.dtos.ReviewDTO;
import raven.iss.data.model.Status;
import raven.iss.data.services.interfaces.PaperService;
import raven.iss.web.security.permissions.CPAPermission;
import raven.iss.web.security.permissions.CPPermission;
import raven.iss.web.security.permissions.ChairPermission;

import java.util.List;

import static raven.iss.web.controllers.UtilsHelper.getUsername;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final PaperService paperService;

    @CPAPermission
    @GetMapping("/api/conferences/{cid}/papers/{pid}/reviews")
    public List<ReviewDTO> getAll(@PathVariable Integer cid, @PathVariable Integer pid) {
        return paperService.findAllReviews(cid, pid, getUsername());
    }

    @CPPermission
    @PostMapping("/api/conferences/{cid}/papers/{pid}/reviews")
    public void review(@PathVariable Integer cid, @PathVariable Integer pid, @RequestBody ReviewDTO reviewDTO) {
        String username = getUsername();
        this.paperService.review(username, cid, pid, reviewDTO);
    }

    @ChairPermission
    @PutMapping("/api/conferences/{cid}/papers/{pid}/{status}")
    public void acceptOrDecline(@PathVariable Integer cid, @PathVariable Integer pid,
                                @PathVariable("status") Status status) {
        this.paperService.acceptOrDecline(cid, pid, status, getUsername());
    }

    @ChairPermission
    @PutMapping("/api/conferences/{cid}/papers")
    public void analysePapers(@PathVariable Integer cid) {
        this.paperService.analysePapers(cid);
    }

}
