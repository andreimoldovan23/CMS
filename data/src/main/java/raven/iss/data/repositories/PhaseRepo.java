package raven.iss.data.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import raven.iss.data.model.Phase;

import java.time.LocalDateTime;
import java.util.Set;

public interface PhaseRepo extends CrudRepository<Phase, Integer> {

    @Query("select p from Phase p " +
            "inner join Conference c " +
            "on p.conf = c where c.id = :id")
    Set<Phase> findPhasesByConfId(@Param("id") Integer id);

    @Query("select p.isActive from Phase p inner join Conference c " +
            "on p.conf = c where c.id = ?1 and p.name = ?2 and p.deadline >= ?3")
    Boolean isPhaseActiveAndBeforeDeadline(Integer id, String name, LocalDateTime now);

}
