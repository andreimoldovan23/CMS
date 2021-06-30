package raven.iss.data.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.Chair;

public interface ChairRepo extends CrudRepository<Chair, Integer> {

    @Query(value = "select c from Chair c inner join User u on c.user = u " +
            "inner join Conference conf on c.conf = conf where u.username = ?1 and conf.id = ?2")
    Chair findByUsernameAndConfId(String username, Integer id);

}
