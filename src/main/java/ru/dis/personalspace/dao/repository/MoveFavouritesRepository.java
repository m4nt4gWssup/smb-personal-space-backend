package ru.dis.personalspace.dao.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import ru.dis.personalspace.dao.models.PersonalSpaceFavourite;
import ru.dis.personalspace.dao.models.PersonalSpaceFavourite_;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MoveFavouritesRepository {

    private final EntityManager em;

    public List<Float> findInsertBetweenOrders(Long folderId, int firstResult) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Float> cq = cb.createQuery(Float.class);
        Root<PersonalSpaceFavourite> root = cq.from(PersonalSpaceFavourite.class);

        cq.select(root.get(PersonalSpaceFavourite_.order))
                .where(cb.equal(root.get(PersonalSpaceFavourite_.folderId), folderId))
                .orderBy(cb.desc(root.get(PersonalSpaceFavourite_.order)));

        int maxResults = (firstResult == 0) ? 1 : 2;

        TypedQuery<Float> typedQuery = em.createQuery(cq)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults);

        return typedQuery.getResultList();
    }
}
