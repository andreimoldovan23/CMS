package raven.iss.data.repositories.authorFragments;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.QueryHints;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.model.*;
import raven.iss.data.repositories.RepoSupport;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Slf4j
@SuppressWarnings("DuplicatedCode")
public class AuthorRepoImpl extends RepoSupport<Author, Integer> implements AuthorRepoCustom {
    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Author findByUsernameAndConfIdWithPapers(String username, Integer id) {
        log.trace("Searching author by username and conf id with PAPERS - {}, {}", username, id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Author> query = builder.createQuery(Author.class);
        query.distinct(Boolean.TRUE);
        Root<Author> root = query.from(Author.class);
        Fetch<Author, Paper> fetchedPapers = root.fetch(Author_.papers, JoinType.LEFT);
        fetchedPapers.fetch(Paper_.topics, JoinType.LEFT);
        fetchedPapers.fetch(Paper_.keywords, JoinType.LEFT);
        query.select(root)
                .where(builder.and(
                        builder.equal(root.get(Author_.user).get(User_.username), username),
                        builder.equal(root.get(Author_.conf).get(Conference_.id), id)
                ));
        TypedQuery<Author> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Author findByUsernameAndConfIdWithSessions(String username, Integer id) {
        log.trace("Searching author by username and conf id with SESSIONS - {}, {}", username, id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Author> query = builder.createQuery(Author.class);
        query.distinct(Boolean.TRUE);
        Root<Author> root = query.from(Author.class);
        root.fetch(Author_.sessionSpeakers, JoinType.LEFT);
        query.select(root)
                .where(builder.and(
                        builder.equal(root.get(Author_.user).get(User_.username), username),
                        builder.equal(root.get(Author_.conf).get(Conference_.id), id)
                ));
        TypedQuery<Author> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public List<Author> findAllFromConfWithPapers(Integer cid) {
        log.trace("Searching all author from conf cid {} with papers", cid);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Author> query = builder.createQuery(Author.class);
        query.distinct(Boolean.TRUE);
        Root<Author> root = query.from(Author.class);
        root.fetch(Author_.papers, JoinType.LEFT);
        query.select(root)
                .where(builder.equal(root.get(Author_.conf).get(Conference_.id), cid));
        TypedQuery<Author> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResultList(typedQuery);
    }
}
