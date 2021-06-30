package raven.iss.data.repositories.sessionFragments;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.QueryHints;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.model.*;
import raven.iss.data.repositories.RepoSupport;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

@Slf4j
@SuppressWarnings("DuplicatedCode")
public class SessionRepoImpl extends RepoSupport<Session, Integer> implements SessionRepoCustom {

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Session findByIdSpeakersWatchers(Integer id) {
        log.trace("Searching session by id - {} WITH SPEAKERS, WATCHERS, NESTED SESSIONS FOR BOTH", id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Session> query = builder.createQuery(Session.class);
        query.distinct(Boolean.TRUE);
        Root<Session> root = query.from(Session.class);
        Fetch<Session, Author> fetchedSpeakers = root.fetch(Session_.speakers, JoinType.LEFT);
        Fetch<Session, Listener> fetchedWatchers = root.fetch(Session_.watchers, JoinType.LEFT);
        fetchedSpeakers.fetch(Author_.sessionSpeakers, JoinType.LEFT);
        fetchedWatchers.fetch(Listener_.attendingSections, JoinType.LEFT);
        query.select(root)
                .where(builder.equal(root.get(Session_.id), id));
        TypedQuery<Session> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Session findByIdSpeakers(Integer id) {
        log.trace("Searching session by id - {} WITH SPEAKERS", id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Session> query = builder.createQuery(Session.class);
        query.distinct(Boolean.TRUE);
        Root<Session> root = query.from(Session.class);
        Fetch<Session, Author> fetchedSpeakers = root.fetch(Session_.speakers, JoinType.LEFT);
        fetchedSpeakers.fetch(Author_.sessionSpeakers, JoinType.LEFT);
        query.select(root)
                .where(builder.equal(root.get(Session_.id), id));
        TypedQuery<Session> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

    @Transactional
    @Override
    public Session findByIdWatchers(Integer id) {
        log.trace("Searching session by id - {} WITH WATCHERS", id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Session> query = builder.createQuery(Session.class);
        query.distinct(Boolean.TRUE);
        Root<Session> root = query.from(Session.class);
        Fetch<Session, Listener> fetchedWatchers = root.fetch(Session_.watchers, JoinType.LEFT);
        fetchedWatchers.fetch(Listener_.attendingSections, JoinType.LEFT);
        query.select(root)
                .where(builder.equal(root.get(Session_.id), id));
        TypedQuery<Session> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }

}
