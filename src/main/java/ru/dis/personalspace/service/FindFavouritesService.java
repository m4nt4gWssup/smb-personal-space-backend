package ru.dis.personalspace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ru.dis.personalspace.dao.dto.FavouriteDto;
import ru.dis.personalspace.dao.dto.response.FavouriteRsDto;
import ru.dis.personalspace.dao.dto.response.FavouritesPageRsDto;
import ru.dis.personalspace.dao.models.PersonalSpaceFavouriteBackup;
import ru.dis.personalspace.dao.repository.FindFavouritesRepository;
import ru.dis.personalspace.dao.repository.PersonalSpaceFavouriteBackupRepository;
import ru.dis.personalspace.dao.repository.PersonalSpaceFavouriteRepository;
import ru.dis.personalspace.enums.FavouriteType;
import ru.dis.personalspace.factory.FavouritesProcessor;
import ru.dis.personalspace.factory.FavouritesProcessorFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindFavouritesService {
    private final FindFavouritesRepository findFavouritesRepository;
    private final PersonalSpaceFavouriteRepository favouriteRepository;
    private final PersonalSpaceFavouriteBackupRepository favouriteBackupRepository;
    private final FavouritesProcessorFactory favouritesFactory;

    //TODO доработать для ситуаций, когда какая-то из доработок отключена
    public FavouritesPageRsDto<FavouriteRsDto> findFavourites(Long profileId, Long folderId, FavouriteType type, PageRequest pageRequest) {

        FavouritesPageRsDto<FavouriteDto> favourites = findFavouritesRepository.findFavourites(profileId, folderId, type, pageRequest);

        // TODO перенести из основной части кода в utils
        if (favourites.hasContent()) {
            log.debug("Found {} favourites for profileId: {}, folderId: {}, type: {}. Page: {}/{}. Details:",
                    favourites.getTotalElements(), profileId, folderId, type, favourites.getNumber() + 1, favourites.getTotalPages());

            favourites.getContent().forEach(fav -> log.debug(
                    "- Favourite: [objectId: {}, type: {}]",
                    fav.getObjectId(), fav.getType()));
        } else {
            log.debug("No favourites found for profileId: {}, folderId: {}, type: {}. Page: {}/{}.",
                    profileId, folderId, type, favourites.getNumber() + 1, favourites.getTotalPages());
        }

        // sortedObjectIds и favouriteRsDtosSortedMap необходимы, т.к. объекты берутся пачками из разных доработок, но необходимо сохранить сортировку
        List<String> sortedObjectIds = favourites.stream()
                .map(FavouriteDto::getObjectId)
                .collect(Collectors.toList());

        log.debug("Sorted object IDs extracted from favourites: {}", sortedObjectIds);

        TreeMap<String, FavouriteRsDto> favouriteRsDtosSortedMap = new TreeMap<>(Comparator.comparingInt(key -> {
            int index = sortedObjectIds.indexOf(key);
            return index != -1 ? index : Integer.MAX_VALUE;
        }));

        if (type == null) {
            fillFavouritesAllTypes(profileId, favourites.getContent(), favouriteRsDtosSortedMap);
            return new FavouritesPageRsDto<>(new ArrayList<>(favouriteRsDtosSortedMap.values()), pageRequest, favourites.getTotalElements());
        }

        fillFavouritesByType(profileId, type, favourites.stream().map(FavouriteDto::getObjectId).collect(Collectors.toList()), favouriteRsDtosSortedMap);

        List<FavouriteRsDto> favouriteRsDtos = new ArrayList<>(favouriteRsDtosSortedMap.values());
        log.debug("Final keys in favouriteRsDtosSortedMap: {}", favouriteRsDtosSortedMap.keySet());

        boolean showAllFolders = (folderId == null);
        List<Long> foldersToHighlight;
        if (showAllFolders) {
            foldersToHighlight = favouriteRepository.findFavouritesFolderIds(new ArrayList<>(favouriteRsDtosSortedMap.keySet()), profileId, type);
        } else {
            foldersToHighlight = null;
        }

        return new FavouritesPageRsDto<>(favouriteRsDtos, pageRequest, favourites.getTotalElements(), foldersToHighlight);
    }

    private void fillFavouritesAllTypes(Long profileId, List<FavouriteDto> favourites, TreeMap<String, FavouriteRsDto> favouriteRsDtosSortedMap) {
        Map<FavouriteType, List<String>> favouritesObjectsIdsGroupedByType = favourites.stream()
                .collect(Collectors.groupingBy(FavouriteDto::getType,
                        Collectors.mapping(FavouriteDto::getObjectId, Collectors.toList())));

        for (Map.Entry<FavouriteType, List<String>> group : favouritesObjectsIdsGroupedByType.entrySet()) {
            fillFavouritesByType(profileId, group.getKey(), group.getValue(), favouriteRsDtosSortedMap);
        }
    }

    private void fillFavouritesByType(Long profileId, FavouriteType type, List<String> objectIds,
                                      TreeMap<String, FavouriteRsDto> favouriteRsDtosSortedMap) {
        FavouritesProcessor favouritesProcessor = favouritesFactory.getProcessor(type);
        if (favouritesProcessor == null) return;

        List<FavouriteRsDto> favouriteRsDtos = favouritesProcessor.findObjects(profileId, objectIds);
        favouriteRsDtos.forEach(dto -> {
            favouritesProcessor.processLink(dto);
            favouriteRsDtosSortedMap.put(favouritesProcessor.getObjectId(dto), dto);
        });

        if (objectIds.size() == favouriteRsDtos.size()) {
            return;
        }

        List<String> notFoundObjectIds = new ArrayList<>(objectIds);

        for (FavouriteRsDto favouriteRsDto : favouriteRsDtos) {
            notFoundObjectIds.remove(favouritesProcessor.getObjectId(favouriteRsDto));
        }

        List<PersonalSpaceFavouriteBackup> favouriteBackups = favouriteBackupRepository.findByObjectIdInAndType(notFoundObjectIds, type);

        for (PersonalSpaceFavouriteBackup favouriteBackup : favouriteBackups) {
            FavouriteRsDto dto = favouritesProcessor.createFavouriteFromBackup(favouriteBackup);
            favouriteRsDtosSortedMap.put(favouritesProcessor.getObjectId(dto), dto);
        }
    }
}
