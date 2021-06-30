package raven.iss.data.repositories.paperFragments;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.Paper;

import java.util.List;

public interface PaperRepo extends CrudRepository<Paper, Integer>, PaperRepoCustom {
    @Query("select distinct p from Paper p join p.authors a left join fetch p.authors" +
            " where a.id in " +
            "(select at.id from Author at where at.conf in " +
            "(select c from Conference c where c.id = ?1))")
    List<Paper> findAllByConfId(Integer cid);
}
