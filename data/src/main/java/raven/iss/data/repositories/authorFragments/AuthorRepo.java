package raven.iss.data.repositories.authorFragments;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.Author;

import java.util.List;

public interface AuthorRepo extends CrudRepository<Author, Integer>, AuthorRepoCustom {
    @Query(value = "select a from Author a " +
            "inner join User u on a.user = u inner join Conference c on a.conf = c where u.username = ?1 " +
            "and c.id = ?2")
    Author findByUsernameAndConfId(String username, Integer id);

    @Query(value = "select a from Author a inner join Conference c on a.conf = c where c.id = ?1")
    List<Author> findAllByConfId(Integer id);
}
