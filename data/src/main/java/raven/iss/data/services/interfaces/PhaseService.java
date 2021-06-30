package raven.iss.data.services.interfaces;

import raven.iss.data.api.dtos.PhaseDTO;

import java.util.List;

public interface PhaseService {

    void addPhase(Integer id, PhaseDTO phaseDTO);
    PhaseDTO getPhase(Integer id);
    void updatePhase(Integer cid, Integer id, PhaseDTO phaseDTO);
    void deletePhase(Integer cid, Integer id);
    List<PhaseDTO> getAll(Integer cid);
    void startPhase(Integer id);

}
