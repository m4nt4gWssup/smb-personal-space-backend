package ru.dis.personalspace.factory;

import ru.dis.personalspace.dao.dto.response.FavouriteRsDto;
import ru.dis.personalspace.dao.models.PersonalSpaceFavouriteBackup;

import java.util.Collection;
import java.util.List;

public interface FavouritesProcessor {
    String DELETED = "Удалено";

    List<FavouriteRsDto> findObjects(Long profileId, Collection<String> objectIds);

    String getObjectId(FavouriteRsDto dto);

    default FavouriteRsDto createFavouriteFromBackup(PersonalSpaceFavouriteBackup backup) {
        return null;
    }

    default void processLink(FavouriteRsDto dto) {
    }

    default String getAdditionalInformation(String objectId) {
        return "";
    }
}
