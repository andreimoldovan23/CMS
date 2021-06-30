package raven.iss.data.repositories.listenerFragments;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.Listener;

public interface ListenerRepo  extends CrudRepository<Listener, Integer>, ListenerRepoCustom {
    @Query(value = "select l from Listener l inner join User u on l.user = u inner" +
            " join Conference c on l.conf = c where u.username = ?1 and c.id = ?2")
    Listener findByUsernameAndConfId(String username, Integer id);
}
