package ru.dis.personalspace.dao.repository.kms;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dis.personalspace.dao.models.kms.ProfileImpl;
import ru.disgroup.kms.common.core.domain.dao.kms.ProfileRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepositoryImpl extends ProfileRepository<ProfileImpl> {
    @Query(value = "SELECT p.id FROM ProfileImpl p join ProfileToPGroupImpl ptpg on p.id = ptpg.profileId WHERE p.id = :profileId and ptpg.groupId in :groups")
    List<Long> findProfileInGroups(Long profileId, List<Long> groups);
}