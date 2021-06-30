package raven.iss.data.services.interfaces;

import raven.iss.data.api.dtos.AuthorDTO;
import raven.iss.data.api.dtos.PaperDTO;
import raven.iss.data.api.dtos.ReviewDTO;
import raven.iss.data.model.Status;

import java.util.List;

public interface PaperService {
    PaperDTO add(PaperDTO paperDTO, Integer confId, String username);
    void delete(Integer confId, Integer paperId);
    void update(PaperDTO paperDTO, Integer paperId);
    List<PaperDTO> findAll(Integer confId);
    PaperDTO findOne(Integer paperId);
    void review(String username, Integer confId, Integer paperId, ReviewDTO reviewDTO);
    List<ReviewDTO> findAllReviews(Integer confId, Integer paperId, String username);
    void acceptOrDecline(Integer cid, Integer pid, Status status, String username);

    void addAuthor(Integer cid, Integer pid, AuthorDTO authorDTO, String username);
    List<AuthorDTO> getAuthors(Integer cid, Integer pid, String username);

    void saveFile(Integer pid, String fileLocation);
    byte[] getFile(Integer pid);

    void savePresentationFile(Integer pid, String fileLocation);
    byte[] getPresentationFile(Integer pid);

    void analysePapers(Integer cid);

    List<PaperDTO> findAllOfAuthor(Integer cid, String username);

    List<PaperDTO> findPapersToReview(Integer cid, String username);
}
