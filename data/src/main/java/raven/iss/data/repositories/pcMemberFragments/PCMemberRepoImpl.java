package raven.iss.data.repositories.pcMemberFragments;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.QueryHints;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.model.*;
import raven.iss.data.repositories.RepoSupport;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

@Slf4j
public class PCMemberRepoImpl extends RepoSupport<PCMember, Integer> implements PCMemberRepoCustom {
    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public PCMember findByUsernameAndConfIdWithPapers(String username, Integer id) {
        log.trace("Searching pcmember by username and conf id with PAPERS - {}, {}", username, id);

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PCMember> query = builder.createQuery(PCMember.class);
        query.distinct(Boolean.TRUE);
        Root<PCMember> root = query.from(PCMember.class);
        Fetch<PCMember, Paper> fetchToReview = root.fetch(PCMember_.toReview, JoinType.LEFT);
        fetchToReview.fetch(Paper_.shouldReview, JoinType.LEFT);
        query.select(root)
                .where(builder.and(
                        builder.equal(root.get(PCMember_.user).get(User_.username), username),
                        builder.equal(root.get(PCMember_.conf).get(Conference_.id), id)
                ));
        TypedQuery<PCMember> typedQuery = getEntityManager().createQuery(query);
        typedQuery.setHint(QueryHints.HINT_CACHEABLE, true);
        return getResult(typedQuery);
    }
}
