package raven.iss.data.repositories.paperFragments;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.QueryHints;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.model.*;
import raven.iss.data.repositories.RepoSupport;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SetAttribute;
import java.util.List;

@Slf4j
public class PaperRepoImpl extends RepoSupport<Paper, Integer> implements PaperRepoCustom {

    private Paper performFindByIdQuery(List<SetAttribute<? super Paper, ?>> fetchAttributes,
                                            Integer id) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Paper> query = builder.createQuery(Paper.class);
        query.distinct(Boolean.TRUE);
        Root<Paper> root = query.from(Paper.class);
        fetchAttributes.forEach(attribute -> root.fetch(attribute, JoinType.LEFT));
        query.select(root)
                .where(builder.equal(root.get(Paper_.id), id));
        TypedQuery<Paper> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Paper findByIdWithMembersAndAuthors(Integer id) {
        log.trace("Searching paper by id - {} WITH PCMEMBERS AND AUTHORS", id);

        return performFindByIdQuery(List.of(Paper_.shouldReview, Paper_.authors), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Paper findByIdWithMembersAndReviews(Integer id) {
        log.trace("Searching paper by id - {} WITH PCMEMBERS AND REVIEWS", id);

        return performFindByIdQuery(List.of(Paper_.shouldReview, Paper_.reviews), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Paper findByIdWithAuthors(Integer id) {
        log.trace("Searching paper by id - {} WITH AUTHORS AND REVIEWERS, AUTHORS - PAPERS, REVIEWERS - PAPERS", id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Paper> query = builder.createQuery(Paper.class);
        query.distinct(Boolean.TRUE);
        Root<Paper> root = query.from(Paper.class);
        Fetch<Paper, Author> fetch = root.fetch(Paper_.AUTHORS, JoinType.LEFT);
        fetch.fetch(Author_.papers, JoinType.LEFT);
        Fetch<Paper, PCMember> fetch1 = root.fetch(Paper_.shouldReview, JoinType.LEFT);
        fetch1.fetch(PCMember_.toReview, JoinType.LEFT);
        query.select(root)
                .where(builder.equal(root.get(Paper_.id), id));
        TypedQuery<Paper> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Paper findByIdWithTopicsAndKeywords(Integer id) {
        log.trace("Searching paper by id - {} WITH TOPICS AND KEYWORDS", id);

        return performFindByIdQuery(List.of(Paper_.topics, Paper_.keywords), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public List<Paper> findAllByConfIdWithTopicsAndKeywords(Integer id) {
        log.trace("Searching all papers by conf id - {} WITH TOPICS AND KEYWORDS", id);

        TypedQuery<Paper> query = getEntityManager().createQuery(
                "select distinct p from Paper p join p.authors a left join fetch p.topics left join fetch p.keywords" +
                " where a.id in " +
                "(select at.id from Author at where at.conf in " +
                "(select c from Conference c where c.id = ?1))", Paper.class);
        query.setParameter(1, id);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        return getResultList(query);
    }

}
