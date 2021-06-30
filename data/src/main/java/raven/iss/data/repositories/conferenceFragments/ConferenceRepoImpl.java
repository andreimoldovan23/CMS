package raven.iss.data.repositories.conferenceFragments;

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
public class ConferenceRepoImpl extends RepoSupport<Conference, Integer> implements ConferenceRepoCustom {

    private Conference performFindByIdQuery(List<SetAttribute<? super Conference, ?>> fetchAttributes,
                                            Integer id) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Conference> query = builder.createQuery(Conference.class);
        query.distinct(Boolean.TRUE);
        Root<Conference> root = query.from(Conference.class);
        fetchAttributes.forEach(attribute -> root.fetch(attribute, JoinType.LEFT));
        query.select(root)
                .where(builder.equal(root.get(Conference_.id), id));
        TypedQuery<Conference> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Conference findByIdWithChairs(Integer id) {
        log.trace("Searching conference by id - {} WITH CHAIRS", id);
        return performFindByIdQuery(List.of(Conference_.chairs), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Conference findByIdWithPCMembers(Integer id) {
        log.trace("Searching conference by id - {} WITH PCMEMBERS", id);
        return performFindByIdQuery(List.of(Conference_.pcMembers), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Conference findByIdWithPhases(Integer id) {
        log.trace("Searching conference by id - {} WITH PHASES", id);
        return performFindByIdQuery(List.of(Conference_.phases), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Conference findByIdWithRooms(Integer id) {
        log.trace("Searching conference by id - {} WITH ROOMS", id);
        return performFindByIdQuery(List.of(Conference_.rooms), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Conference findByIdWithAuthors(Integer id) {
        log.trace("Searching conference by id - {} WITH AUTHORS", id);
        return performFindByIdQuery(List.of(Conference_.authors), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Conference findByIdWithListeners(Integer id) {
        log.trace("Searching conference by id - {} WITH LISTENERS", id);
        return performFindByIdQuery(List.of(Conference_.listeners), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Conference findByIdWithSCR(Integer id) {
        log.trace("Searching conference by id - {} WITH SESSIONS, ROOMS, CHAIRS", id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Conference> query = builder.createQuery(Conference.class);
        query.distinct(Boolean.TRUE);
        Root<Conference> root = query.from(Conference.class);
        root.fetch(Conference_.sessions, JoinType.LEFT);
        root.fetch(Conference_.rooms, JoinType.LEFT);
        Fetch<Conference, Chair> chairsFetched = root.fetch(Conference_.chairs, JoinType.LEFT);
        chairsFetched.fetch(Chair_.sessions, JoinType.LEFT);
        query.select(root)
                .where(builder.equal(root.get(Conference_.id), id));
        TypedQuery<Conference> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Conference findByIdWithSessions(Integer id) {
        log.trace("Searching conference by id - {} WITH SESSIONS", id);
        return performFindByIdQuery(List.of(Conference_.sessions), id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Conference findByIdWithMembersPapers(Integer id) {
        log.trace("Searching conference by id - {} WITH MEMBERS AND CORRESPONDING PAPERS", id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Conference> query = builder.createQuery(Conference.class);
        query.distinct(Boolean.TRUE);
        Root<Conference> root = query.from(Conference.class);
        Fetch<Conference, PCMember> pcMemberFetch = root.fetch(Conference_.pcMembers, JoinType.LEFT);
        pcMemberFetch.fetch(PCMember_.toReview, JoinType.LEFT);
        query.select(root)
                .where(builder.equal(root.get(Conference_.id), id));
        TypedQuery<Conference> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

}
