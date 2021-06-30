package raven.iss.web.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import raven.iss.data.constants.Messages;
import raven.iss.data.exceptions.BadRequestException;
import raven.iss.data.exceptions.InternalErrorException;
import raven.iss.data.services.interfaces.PaperService;
import raven.iss.web.security.permissions.AuthorPermission;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ControllerPaperFile {
    private final PaperService paperService;

    @AuthorPermission
    @PostMapping("/api/conferences/{cid}/papers/{pid}/file")
    public void uploadFile(@PathVariable Integer cid, @PathVariable Integer pid,
                            @RequestParam("paperFile") MultipartFile paperFile) {
        try {
            Tika tika = new Tika();
            String type = tika.detect(paperFile.getBytes());
            if (!(type.equals(MediaType.APPLICATION_PDF_VALUE))) {
                throw new BadRequestException(Messages.cannotUpload);
            }

            String newFileName = "FileStorage\\" + pid + " - paperFile.pdf";
            FileUtils.writeByteArrayToFile(new File(newFileName),
                    paperFile.getBytes());

            paperService.saveFile(pid, newFileName);
        } catch (IOException e) {
            throw new InternalErrorException(Messages.cannotUpload);
        }
    }

    @GetMapping(value = "/api/conferences/{cid}/papers/{pid}/file",
        produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getFile(@PathVariable Integer cid, @PathVariable Integer pid) {
        return this.paperService.getFile(pid);
    }

}
