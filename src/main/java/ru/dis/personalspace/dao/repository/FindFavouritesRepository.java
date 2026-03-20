package ru.dis.personalspace.dao.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.dis.personalspace.dao.dto.FavouriteDto;
import ru.dis.personalspace.dao.dto.response.FavouritesPageRsDto;
import ru.dis.personalspace.dao.models.PersonalSpaceFavourite;
import ru.dis.personalspace.dao.models.PersonalSpaceFavourite_;
import ru.dis.personalspace.enums.FavouriteType;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;


@Repository
@Slf4j
@RequiredArgsConstructor
public class FindFavouritesRepository {
    private final EntityManager em;

    public FavouritesPageRsDto<FavouriteDto> findFavourites(Long profileId, Long folderId, FavouriteType type,
                                                            PageRequest pageRequest) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FavouriteDto> cq = cb.createQuery(FavouriteDto.class);
        Root<PersonalSpaceFavourite> root = cq.from(PersonalSpaceFavourite.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get(PersonalSpaceFavourite_.profileId), profileId));

        if (folderId != null) {
            predicates.add(cb.equal(root.get(PersonalSpaceFavourite_.folderId), folderId));
        }
        if (type != null) {
            predicates.add(cb.equal(root.get(PersonalSpaceFavourite_.type), type));
        }

        cq.select(cb.construct(FavouriteDto.class,
                root.get(PersonalSpaceFavourite_.objectId),
                root.get(PersonalSpaceFavourite_.type)));

        if (folderId == null) {
            cq.groupBy(root.get(PersonalSpaceFavourite_.objectId), root.get(PersonalSpaceFavourite_.type));
            cq.orderBy(cb.desc(cb.greatest(root.get(PersonalSpaceFavourite_.creationDateTime))));
        } else {
            cq.orderBy(cb.desc(root.get(PersonalSpaceFavourite_.order)));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<FavouriteDto> typedQuery = em.createQuery(cq);
        typedQuery.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
        typedQuery.setMaxResults(pageRequest.getPageSize());

        return new FavouritesPageRsDto<>(typedQuery.getResultList(), pageRequest, findCount(profileId, folderId, type));
    }

    //TODO кэширование
    private Long findCount(Long profileId, Long folderId, FavouriteType type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<PersonalSpaceFavourite> countRoot = cq.from(PersonalSpaceFavourite.class);

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<PersonalSpaceFavourite> root = subquery.from(PersonalSpaceFavourite.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get(PersonalSpaceFavourite_.profileId), profileId));

        if (folderId != null) {
            predicates.add(cb.equal(root.get(PersonalSpaceFavourite_.folderId), folderId));
        }

        if (type != null) {
            predicates.add(cb.equal(root.get(PersonalSpaceFavourite_.type), type));
        }

        subquery.select(cb.max(root.get(PersonalSpaceFavourite_.id)))
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(root.get(PersonalSpaceFavourite_.objectId), root.get(PersonalSpaceFavourite_.type));

        cq.select(cb.count(countRoot)).where(cb.in(countRoot.get(PersonalSpaceFavourite_.id)).value(subquery));
        return em.createQuery(cq).getSingleResult();
    }
}
