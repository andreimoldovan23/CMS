package raven.iss.data.repositories.authorFragments;

import raven.iss.data.model.Author;

import java.util.List;

public interface AuthorRepoCustom {
    Author findByUsernameAndConfIdWithPapers(String username, Integer id);
    Author findByUsernameAndConfIdWithSessions(String username, Integer id);
    List<Author> findAllFromConfWithPapers(Integer cid);
}
