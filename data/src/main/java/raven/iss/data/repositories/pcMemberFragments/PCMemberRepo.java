package raven.iss.data.repositories.pcMemberFragments;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import raven.iss.data.model.PCMember;

import java.util.List;

public interface PCMemberRepo extends CrudRepository<PCMember, Integer>, PCMemberRepoCustom {
    @Query(value = "select p from PCMember p inner join User u on p.user = u inner" +
            " join Conference c on p.conf = c where u.username = ?1 and c.id = ?2")
    PCMember findByUsernameAndConfId(String username, Integer id);

    @Query(value = "select p from PCMember p inner join Conference c on p.conf = c where " +
            "c.id = ?1")
    List<PCMember> findAllByConfId(Integer cid);
}
