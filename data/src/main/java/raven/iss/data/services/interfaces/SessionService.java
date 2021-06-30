package raven.iss.data.services.interfaces;

import raven.iss.data.api.dtos.AuthorDTO;
import raven.iss.data.api.dtos.SessionDTO;
import raven.iss.data.api.dtos.UserDTO;

import java.util.List;

public interface SessionService {

    SessionDTO addSession(Integer id, SessionDTO sessionDTO);
    SessionDTO getSession(Integer id);
    void updateSession(Integer id, SessionDTO sessionDTO);
    void deleteSession(Integer cid, Integer id);
    List<SessionDTO> getAll(Integer id);
    void attendSession(Integer cid, Integer id, String username);

    List<UserDTO> getAllSpeakers(Integer cid, Integer sid);
    void addSpeakerToSession(Integer cid, Integer sid, AuthorDTO authorDTO);
    void deleteSpeakerFromSession(Integer cid, Integer sid, String username);

    boolean isListenerAttending(Integer cid, Integer sid, String username);
}
