package raven.iss.data.repositories.sessionFragments;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.Session;

import java.util.Set;

public interface SessionRepo extends CrudRepository<Session, Integer>, SessionRepoCustom {

    @Query("select s from Session s " +
            "inner join Conference c " +
            "on s.conf = c where c.id = ?1")
    Set<Session> findSessionsByConfId(Integer id);

}
