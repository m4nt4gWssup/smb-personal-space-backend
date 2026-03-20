package ru.dis.personalspace.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dis.personalspace.dao.dto.Projector;
import ru.dis.personalspace.dao.models.PersonalSpaceFolder;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalSpaceFolderRepository extends JpaRepository<PersonalSpaceFolder, Long> {

    @Query(value = "select psf.name from PersonalSpaceFolder psf where psf.id = :folderId")
    Optional<String> findNameById(@Param("folderId") Long folderId);

    @Query("SELECT psf.profileId FROM PersonalSpaceFolder psf WHERE psf.id = :folderId")
    Optional<Long> findProfileIdByFolderId(Long folderId);

    @Query(value = "select max(psf.order) from PersonalSpaceFolder psf where psf.profileId = :profileId and psf.parentFolder.id = :parentId")
    Optional<Float> findMaxOrder(Long profileId, Long parentId);

    @Query(value = "select max(psf.order) from PersonalSpaceFolder psf where psf.profileId = :profileId and psf.parentFolder is null")
    Optional<Float> findMaxOrder(Long profileId);

    @Query(value = "select psf.order from PersonalSpaceFolder psf where psf.id = :folderId")
    Optional<Float> findOrderByFolderId(Long folderId);

    @Modifying
    @Query(value = "update PersonalSpaceFolder psf set psf.order = :order where psf.id = :folderId")
    void updateOrder(Long folderId, float order);

    @Modifying
    @Query(value = "update PersonalSpaceFolder psf set psf.parentFolder = :parentFolder where psf.id = :folderId")
    void updateParentFolder(Long folderId, PersonalSpaceFolder parentFolder);

    @Query(value = "select psf from PersonalSpaceFolder psf where psf.profileId = :profileId and psf.name = 'Избранное'")
    Optional<PersonalSpaceFolder> findFavouritesFolder(Long profileId);

    @Query(nativeQuery = true, value = "with recursive r (     " +
            "          path,   " +
            "          parent_id,   " +
            "          folder_id , " +
            "          folder_order " +
            "      ) as ( " +
            "select " +
            "	array[cast(psf1.folder_name as varchar)], " +
            "	psf1.parent_id, " +
            "	array[psf1.folder_id], " +
            "	array[psf1.folder_order] " +
            "from " +
            "	dis_personal_space_folders psf1 " +
            "where " +
            "	psf1.profile_id = :profileId " +
            "union all " +
            "select " +
            "	array[psf2.folder_name ]|| r.path, " +
            "	psf2.parent_id, " +
            "	array[psf2.folder_id] || r.folder_id, " +
            "	array[psf2.folder_order] || r.folder_order " +
            "from " +
            "	dis_personal_space_folders psf2 " +
            "inner join r on " +
            "	psf2.folder_id = r.parent_id    " +
            "      )     " +
            "      select " +
            "	 	array_to_string(path,'%!%') as folderNames, " +
            "	 	array_to_string(folder_id,'%!%') as folderIds " +
            "from " +
            "	r " +
            "where " +
            "	parent_id is null " +
            "	and not exists ( " +
            "	select " +
            "		* " +
            "	from " +
            "		dis_personal_space_folders psf3 " +
            "	where " +
            "		psf3.parent_id = r.folder_id[array_upper(r.folder_id, 1)]) " +
            "order by " +
            "	r.folder_order[1],"+
            "   r.folder_order[array_upper(r.folder_order, 1)]")
    List<Projector> getTree(Long profileId);

}
