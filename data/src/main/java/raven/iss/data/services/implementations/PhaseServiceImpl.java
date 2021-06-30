package raven.iss.data.services.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.api.dtos.PhaseDTO;
import raven.iss.data.api.mappers.PhaseMapper;
import raven.iss.data.constants.Messages;
import raven.iss.data.exceptions.InternalErrorException;
import raven.iss.data.model.Conference;
import raven.iss.data.model.Phase;
import raven.iss.data.repositories.conferenceFragments.ConferenceRepo;
import raven.iss.data.repositories.PhaseRepo;
import raven.iss.data.services.interfaces.PhaseService;
import raven.iss.data.validators.Helper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class PhaseServiceImpl implements PhaseService {

    private final PhaseMapper phaseMapper;
    private final PhaseRepo phaseRepo;
    private final ConferenceRepo conferenceRepo;

    @Transactional
    @Override
    public void addPhase(Integer id, PhaseDTO phaseDTO) {
        log.trace("Phase service - add - cid {}, data {}", id, phaseDTO.toString());
        Phase phase = phaseMapper.DTOtoPhase(phaseDTO);
        log.trace("Phase service - add - fetch conference");
        Conference conf = (Conference) Helper.checkNull(conferenceRepo.findByIdWithPhases(id), Messages.conferenceNotFound);

        log.trace("Phase service - add - check phase deadline {} against conference start date {}",
                phase.getDeadline(), conf.getStartDate());
        Helper.validateDates(phase.getDeadline(), conf.getStartDate(), Messages.deadlineError);

        phase.setConf(conf);
        conf.getPhases().add(phase);
        log.trace("Phase service - add - saving data...");
        conferenceRepo.save(conf);
    }

    @Transactional(readOnly = true)
    @Override
    public PhaseDTO getPhase(Integer id) {
        log.trace("Phase service - get one - id {}", id);
        return phaseMapper.phaseToDTO((Phase) Helper.checkNull(phaseRepo.findById(id).orElse(null),
                Messages.phaseNotFound));
    }

    private Phase getPhaseFromConf(Conference conference, Integer id) {
        return (Phase) Helper.checkNull(conference.getPhases().stream()
                .filter(phaseConf -> phaseConf.getId().equals(id))
                .findFirst().orElse(null), Messages.phaseNotFound);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public void updatePhase(Integer cid, Integer id, PhaseDTO phaseDTO) {
        log.trace("Phase service - update - cid {}, id {}, data {}", cid, id, phaseDTO.toString());
        Phase phase = (Phase) Helper.checkNull(phaseRepo.findById(id).orElse(null), Messages.phaseNotFound);
        Phase toUpdate = phaseMapper.DTOtoPhase(phaseDTO);
        log.trace("Phase service - update - check phase deadline {} against conference start date {}",
                phase.getDeadline(), phase.getConf().getStartDate());
        Helper.validateDates(toUpdate.getDeadline(), phase.getConf().getStartDate(), Messages.deadlineError);
        phase.setName(toUpdate.getName() != null ? toUpdate.getName() : phase.getName());
        phase.setDeadline(toUpdate.getDeadline() != null ? toUpdate.getDeadline() : phase.getDeadline());
        log.trace("Phase service - update - saving data...");
        phaseRepo.save(phase);
    }

    @Transactional
    @Override
    public void deletePhase(Integer cid, Integer id) {
        log.trace("Phase service - delete - cid {}, id {}", cid, id);
        Conference conf = (Conference) Helper.checkNull(conferenceRepo.findByIdWithPhases(cid), Messages.conferenceNotFound);
        log.trace("Phase service - delete - get phase from conf");
        Phase phase = getPhaseFromConf(conf, id);
        conf.getPhases().remove(phase);
        log.trace("Phase service - delete - saving data...");
        conferenceRepo.save(conf);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PhaseDTO> getAll(Integer cid) {
        log.trace("Phase service - get all - cid {}", cid);
        return phaseRepo.findPhasesByConfId(cid).stream()
                .map(phaseMapper::phaseToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void startPhase(Integer id) {
        log.trace("Phase service - start phase - id {}", id);
        Phase phase = (Phase) Helper.checkNull(phaseRepo.findById(id).orElse(null), Messages.phaseNotFound);
        log.trace("Phase service - start phase - check if already active");
        if (phase.getIsActive()) throw new InternalErrorException(Messages.phaseAlreadyActive);
        if (phase.getDeadline().isBefore(LocalDateTime.now())) throw new InternalErrorException(Messages.phaseFinished);
        phase.setIsActive(true);
        log.trace("Phase service - start phase - saving data...");
        phaseRepo.save(phase);
    }

    @Async
    @Scheduled(fixedRate = 86400000)
    public void checkPhasesDeadline() {
        log.trace("Chrono job - check if deadline of phases is over");
        List<Phase> phases = StreamSupport.stream(phaseRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
        log.trace("Retrieved all phases");
        AtomicInteger count = new AtomicInteger();
        phases.forEach(phase -> {
            if (phase.getDeadline().isBefore(LocalDateTime.now())) {
                phase.setIsActive(false);
                phaseRepo.save(phase);
                count.getAndIncrement();
            }
        });
        log.trace("Performed checks on all phases. Closed {} of them", count.get());
    }

}
