package ru.dis.personalspace.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dis.personalspace.dao.models.PersonalSpaceFavourite;
import ru.dis.personalspace.enums.FavouriteType;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalSpaceFavouriteRepository extends JpaRepository<PersonalSpaceFavourite, Long> {

    @Query(value = "select max(psf.order) from PersonalSpaceFavourite psf where psf.folderId = :folderId")
    Optional<Float> findMaxOrder(@Param("folderId") Long folderId);

    @Query(value = "select psf.order from PersonalSpaceFavourite psf where psf.objectId = :objectId and psf.type = :type and psf.folderId = :folderId")
    Optional<Float> findOrderByObjectId(@Param("objectId") String objectId, @Param("type") FavouriteType type, @Param("folderId") Long folderId);

    @Query(value = "select min(psf.order) from PersonalSpaceFavourite psf " +
            "where psf.order > (select psf2.order from PersonalSpaceFavourite psf2 " +
            "where psf2.objectId = :nextObjectId and psf2.type = :nextObjectType and psf2.folderId = :folderId) " +
            "and psf.folderId = :folderId")
    Optional<Float> findPrevOrder(@Param("nextObjectId") String nextObjectId, @Param("nextObjectType") FavouriteType nextObjectType, @Param("folderId") Long folderId);

    @Query(value = "select max(psf.order) from PersonalSpaceFavourite psf " +
            "where psf.order < (select psf2.order from PersonalSpaceFavourite psf2 " +
            "where psf2.objectId = :prevObjectId and psf2.type = :prevObjectType and psf2.folderId = :folderId) " +
            "and psf.folderId = :folderId")
    Optional<Float> findNextOrder(@Param("prevObjectId") String prevObjectId, @Param("prevObjectType") FavouriteType prevObjectType, Long folderId);

    @Modifying
    @Query(value = "update PersonalSpaceFavourite psf set psf.order = :order where psf.objectId = :objectId and psf.type = :type and psf.folderId = :folderId")
    void updateOrder(float order, String objectId, FavouriteType type, Long folderId);

    Optional<PersonalSpaceFavourite> findByObjectIdAndTypeAndFolderId(String objectId, FavouriteType type, Long folderId);

    @Modifying
    @Query(value = "update PersonalSpaceFavourite psf set psf.folderId = :toFolderId, psf.order = :order where psf.objectId = :objectId and psf.folderId = :fromFolderId")
    void updateFolderId(Long toFolderId, float order, String objectId, Long fromFolderId);

    @Modifying
    @Query(value = "delete from PersonalSpaceFavourite psf where psf.objectId = :objectId and psf.profileId = :profileId and psf.type = :type")
    void deleteFromFavourites(String objectId, Long profileId, FavouriteType type);

    @Modifying
    @Query("delete from PersonalSpaceFavourite psf where psf.objectId = :objectId and psf.type = :type and psf.folderId = :folderId")
    void deleteFromFavourites(String objectId, FavouriteType type, Long folderId);

    @Query(value = "select psf.objectId from PersonalSpaceFavourite psf where psf.objectId = :articleId and psf.profileId = :profileId and psf.type = 'ARTICLE'")
    List<String> findFavouriteArticlesIds(String articleId, Long profileId);

    @Query(value = "select psf.objectId from PersonalSpaceFavourite psf where psf.objectId in :objectIds and psf.profileId = :profileId and psf.type = :type")
    List<String> checkFavourites(List<String> objectIds, Long profileId, FavouriteType type);

    @Query(value = "select psf.objectId from PersonalSpaceFavourite psf where psf.profileId = :profileId and psf.type = :type")
    List<String> findFavouritesIds(Long profileId, FavouriteType type);

    @Query("SELECT psf FROM PersonalSpaceFavourite psf WHERE psf.folderId = :folderId")
    List<PersonalSpaceFavourite> findAllByFolderId(Long folderId);

    @Query(value = "select distinct psf.folderId from PersonalSpaceFavourite psf where psf.profileId = :profileId and psf.type = :type and psf.objectId in (:objectIds)")
    List<Long> findFavouritesFolderIds(@Param("objectIds") List<String> objectIds, @Param("profileId") Long profileId, @Param("type") FavouriteType type);

    boolean existsByObjectIdAndType(String objectId, FavouriteType type);

    boolean existsByObjectIdAndProfileIdAndType(String objectId, Long profileId, FavouriteType type);

    boolean existsByObjectIdAndProfileIdAndTypeAndFolderId(String objectId, Long profileId, FavouriteType type, Long folderId);

}