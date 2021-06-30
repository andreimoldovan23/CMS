package raven.iss.data.entitiesAndDtos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raven.iss.data.api.dtos.PaperDTO;
import raven.iss.data.api.mappers.PaperMapper;
import raven.iss.data.model.Paper;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaperMapperTest {
    private PaperMapper paperMapper;
    private Paper paper;
    private PaperDTO paperDTO;

    private static final Integer id = 10;
    private static final String name = "paperName";

    @BeforeEach
    void setUp() {
        paperMapper = PaperMapper.INSTANCE;
        paper = Paper.builder()
                .name(name)
                .keywords(Set.of("key1", "key2"))
                .topics(Set.of("key1", "key2"))
                .build();
        paper.setId(id);
        paperDTO = paperMapper.paperToDTO(paper);

    }

    @Test
    public void paperToPaperDTO() {
        paperDTO = paperMapper.paperToDTO(paper);
        assertEquals(paperDTO.getName(), name);
        assertEquals(paperDTO.getId(), id);
        assertEquals(paperDTO.getKeywords(), paper.getKeywords());
        assertEquals(paperDTO.getTopics(), paper.getTopics());
    }

    @Test
    public void paperDTOtoPaper() {
        paper = paperMapper.DTOtoPaper(paperDTO);
        assertEquals(paper.getName(), name);
        assertEquals(paper.getId(), id);
        assertEquals(paper.getTopics(), paperDTO.getTopics());
        assertEquals(paper.getKeywords(), paperDTO.getKeywords());
    }

}