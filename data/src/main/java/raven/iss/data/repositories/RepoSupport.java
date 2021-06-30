package raven.iss.data.repositories;

import lombok.Getter;
import lombok.Setter;
import raven.iss.data.model.BaseEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class RepoSupport<T extends BaseEntity<ID>, ID extends Integer> {

    @PersistenceContext
    private EntityManager entityManager;

    protected T getResult(TypedQuery<T> query) {
        T result;
        try {
            result = query.getSingleResult();
        } catch (NoResultException nre) {
            result = null;
        }
        return result;
    }

    protected List<T> getResultList(TypedQuery<T> query) {
        List<T> result;
        try {
            result = query.getResultList();
        } catch (NoResultException nre) {
            result = new ArrayList<>();
        }
        return result;
    }

}
