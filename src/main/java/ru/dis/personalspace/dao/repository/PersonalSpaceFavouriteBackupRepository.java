package ru.dis.personalspace.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.dis.personalspace.dao.models.PersonalSpaceFavouriteBackup;
import ru.dis.personalspace.enums.FavouriteType;

public interface PersonalSpaceFavouriteBackupRepository  extends JpaRepository<PersonalSpaceFavouriteBackup, Long> {

    Optional<PersonalSpaceFavouriteBackup> findByObjectIdAndType(String objectId, FavouriteType type);

    List<PersonalSpaceFavouriteBackup> findByObjectIdInAndType(List<String> objectIds, FavouriteType type);

    void deleteByObjectIdAndType(String objectId,FavouriteType type);
}
