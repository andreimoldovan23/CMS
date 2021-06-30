package raven.iss.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import raven.iss.data.api.dtos.AuthorDTO;
import raven.iss.data.api.dtos.PaperDTO;
import raven.iss.data.services.interfaces.PaperService;
import raven.iss.web.security.permissions.AuthorPermission;
import raven.iss.web.security.permissions.CPAPermission;
import raven.iss.web.security.permissions.IsAuthorPermission;
import raven.iss.web.security.permissions.PCMemberPermission;

import java.util.List;

import static raven.iss.web.controllers.UtilsHelper.getUsername;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class PaperController {

    private final PaperService paperService;

    @IsAuthorPermission
    @PostMapping("/api/conferences/{cid}/papers")
    public PaperDTO addPaper(@PathVariable Integer cid, @Validated @RequestBody PaperDTO paperDTO) {
        return paperService.add(paperDTO, cid, getUsername());
    }

    @AuthorPermission
    @PutMapping("/api/conferences/{cid}/papers/{pid}")
    public void updatePaper(@PathVariable Integer cid, @PathVariable Integer pid,
                            @Validated @RequestBody PaperDTO paperDTO) {
        paperService.update(paperDTO, pid);
    }

    @AuthorPermission
    @DeleteMapping("/api/conferences/{cid}/papers/{pid}")
    public void deletePaper(@PathVariable Integer cid, @PathVariable Integer pid) {
        paperService.delete(cid, pid);
    }

    @CPAPermission
    @GetMapping("/api/conferences/{cid}/papers/{id}")
    public PaperDTO getPaper(@PathVariable Integer cid, @PathVariable Integer id) {
        return paperService.findOne(id);
    }

    @CPAPermission
    @GetMapping("/api/conferences/{cid}/papers")
    public List<PaperDTO> getAll(@PathVariable Integer cid) {
        return paperService.findAll(cid);
    }

    @AuthorPermission
    @PutMapping("/api/conferences/{cid}/papers/{pid}/authors")
    public void addAuthor(@PathVariable Integer cid, @PathVariable Integer pid,
                          @RequestBody AuthorDTO authorDTO) {
        this.paperService.addAuthor(cid, pid, authorDTO, getUsername());
    }

    @CPAPermission
    @GetMapping("/api/conferences/{cid}/papers/{pid}/authors")
    public List<AuthorDTO> getAuthors(@PathVariable Integer cid, @PathVariable Integer pid) {
        return this.paperService.getAuthors(cid, pid, getUsername());
    }

    @IsAuthorPermission
    @GetMapping("/api/conferences/{cid}/papersOfAuthor")
    public List<PaperDTO> getPapersOfAuthor(@PathVariable Integer cid) {
        return this.paperService.findAllOfAuthor(cid, getUsername());
    }

    @PCMemberPermission
    @GetMapping("/api/conferences/{cid}/papersToReview")
    public List<PaperDTO> getPCMemberPapersToReview(@PathVariable Integer cid) {
        return this.paperService.findPapersToReview(cid, getUsername());
    }

}
