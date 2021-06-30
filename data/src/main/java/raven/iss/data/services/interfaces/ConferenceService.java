package raven.iss.data.services.interfaces;

import raven.iss.data.api.dtos.*;

import java.util.List;

public interface ConferenceService {
    List<ConferenceDTO> getAll();
    ConferenceDTO findDTObyId(Integer id);
    ConferenceDTO add(ConferenceDTO dto);
    void edit(Integer id, ConferenceDTO dto);
    void deleteById(Integer id);

    List<PCMemberDTO> getAllPcMembers(Integer cid);
    void addPcMember(Integer cid, PCMemberDTO dto, String currentUsername);

    void bid(Integer cid, String username, Boolean likesToReview);
    void addReviewerToPaper(Integer cid, Integer pid, PCMemberDTO pcMemberDTO);

    void addChair(Integer id, ChairDTO chairDTO);
    List<ChairDTO> getAllChairs(Integer cid);
    void deleteChair(Integer id, String username);

    List<UserDTO> filterNotChair(Integer cid, String currentUsername);
    List<UserDTO> filterNotPcMember(Integer cid, String currentUsername);

    List<PCMemberDTO> getPcMembersNotAssignedSpecificPaper(Integer cid, Integer pid);

    PCMemberDTO getPcMemberBidded(Integer cid, String username);

    List<AuthorDTO> filterAuthorsWithAcceptedPapers(Integer cid);

    List<AuthorDTO> getAllAuthors(Integer cid);
}
