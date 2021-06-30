package raven.iss.data.repositories.listenerFragments;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.QueryHints;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.model.Conference_;
import raven.iss.data.model.Listener;
import raven.iss.data.model.Listener_;
import raven.iss.data.model.User_;
import raven.iss.data.repositories.RepoSupport;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

@Slf4j
public class ListenerRepoImpl extends RepoSupport<Listener, Integer> implements ListenerRepoCustom {
    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Listener findByUsernameAndConfIdWithSessions(String username, Integer id) {
        log.trace("Searching listener by username and conference id - {}, {} WITH SESSIONS", username, id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Listener> query = builder.createQuery(Listener.class);
        query.distinct(Boolean.TRUE);
        Root<Listener> root = query.from(Listener.class);
        root.fetch(Listener_.attendingSections, JoinType.LEFT);
        query.select(root)
                .where(builder.and(
                        builder.equal(root.get(Listener_.conf).get(Conference_.id), id),
                        builder.equal(root.get(Listener_.user).get(User_.username), username)
                ));
        TypedQuery<Listener> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }
}
