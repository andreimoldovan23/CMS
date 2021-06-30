package raven.iss.data.entitiesAndDtos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.ConferenceDTO;
import raven.iss.data.api.dtos.PhaseDTO;
import raven.iss.data.api.mappers.PhaseMapper;
import raven.iss.data.model.Conference;
import raven.iss.data.model.Phase;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhaseMapperTest {

    private PhaseMapper phaseMapper;
    private Phase phase;
    private PhaseDTO phaseDTO;

    private static final Integer id = 10;
    private static final Conference conf = Conference.builder()
            .name("ConfName")
            .startDate(LocalDateTime.of(2020, Month.AUGUST, 29, 12, 30, 40))
            .city("CityName")
            .build();
    private static final String name = "phaseName";
    private static final LocalDateTime deadline = LocalDateTime.of(2020, Month.AUGUST, 29, 12, 30, 40);
    private static final boolean isActive = true;

    @BeforeEach
    public void setUp() {
        phaseMapper = PhaseMapper.INSTANCE;
        phase = Phase.builder()
                .name(name)
                .conf(conf)
                .deadline(deadline)
                .isActive(isActive)
                .build();
        phase.setId(id);
        phaseDTO = phaseMapper.phaseToDTO(phase);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void phaseToPhaseDTO() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd//HH::mm::ss");
        phaseDTO = phaseMapper.phaseToDTO(phase);
        ConferenceDTO confDTO = phaseDTO.getConf();
        assertEquals(conf.getName(), confDTO.getName());
        assertEquals(conf.getCity(), confDTO.getCity());
        assertEquals(conf.getStartDate().format(formatter), confDTO.getStartDate());
        assertEquals(LocalDateTime.parse(phaseDTO.getDeadlineString(), formatter), deadline);
        assertEquals(phaseDTO.getId(), id);
        assertEquals(phaseDTO.getIsActive(), isActive);
        assertEquals(phaseDTO.getName(), name);
    }

    @Test
    public void phaseDTOtoPhase() {
        phase = phaseMapper.DTOtoPhase(phaseDTO);
        assertEquals(conf.getName(), phase.getConf().getName());
        assertEquals(conf.getCity(), phase.getConf().getCity());
        assertEquals(conf.getStartDate(), phase.getConf().getStartDate());
        assertEquals(phase.getDeadline(), deadline);
        assertEquals(phase.getId(), id);
        assertEquals(phase.getIsActive(), isActive);
        assertEquals(phase.getName(), name);
    }

}