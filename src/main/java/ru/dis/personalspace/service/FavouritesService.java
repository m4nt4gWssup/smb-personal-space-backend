package ru.dis.personalspace.service;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ru.dis.personalspace.config.properties.SmbPersonalSpaceProperties;
import ru.dis.personalspace.dao.dto.FavouriteDto;
import ru.dis.personalspace.dao.models.PersonalSpaceFavourite;
import ru.dis.personalspace.dao.models.PersonalSpaceFavouriteBackup;
import ru.dis.personalspace.dao.repository.MoveFavouritesRepository;
import ru.dis.personalspace.dao.repository.PersonalSpaceFavouriteBackupRepository;
import ru.dis.personalspace.dao.repository.PersonalSpaceFavouriteRepository;
import ru.dis.personalspace.dao.repository.kms.ProfileRepositoryImpl;
import ru.dis.personalspace.enums.FavouriteType;
import ru.dis.personalspace.factory.FavouritesProcessor;
import ru.dis.personalspace.factory.FavouritesProcessorFactory;
import ru.disgroup.kms.common.core.domain.exceptions.response.ConflictException;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavouritesService {
    private final PersonalSpaceFavouriteRepository favouriteRepository;
    private final PersonalSpaceFavouriteBackupRepository favouriteBackupRepository;
    private final ProfileRepositoryImpl profileRepository;
    private final SmbPersonalSpaceProperties.SmbPersonalSpace smbPersonalSpaceProperties;
    private final MoveFavouritesRepository moveFavouritesRepository;
    private final SmbPersonalSpaceProperties.SmbPersonalSpace smbPersonalSpace;

    private final FavouritesProcessorFactory favouritesFactory;

    private final Set<FavouriteType> noBackupTypes = new HashSet<>(Arrays.asList(FavouriteType.NEWS, FavouriteType.ARTICLE));

    @Transactional
    public void addAndBackupFavourite(String objectId, String title, Long profileId, FavouriteType type, Long folderId) {

        log.info("Start processing: saving OBJECT (id: {}, type: {}) to Favourites. Folder: '{}'. USER: '{}'", objectId, type, folderId, profileId);
        try {
            favouriteRepository.save(
                    PersonalSpaceFavourite.builder()
                            .objectId(objectId)
                            .profileId(profileId)
                            .type(type)
                            .folderId(folderId)
                            .order(findMaxOrder(folderId))
                            .build()
            );

            log.info("Successfully ADD OBJECT (id: {}, type: {}). Folder: '{}'. USER: '{}'", objectId, type, folderId, profileId);

            if (!noBackupTypes.contains(type)) {
                FavouritesProcessor favouritesProcessor = favouritesFactory.getProcessor(type);

                if (!favouriteBackupRepository.findByObjectIdAndType(objectId, type).isPresent()) {
                    favouriteBackupRepository.save(PersonalSpaceFavouriteBackup.builder()
                            .objectId(objectId)
                            .title(title)
                            .additionalInformation(favouritesProcessor.getAdditionalInformation(objectId))
                            .type(type)
                            .build());
                }
            }
        } catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException occurred while adding favourite. objectId: {}, folderId: {}", objectId, folderId);

            // Проверка на уникальность
            if (containsConstraintViolation(e, "dis_personal_space_unique")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Can't add - duplicate favourite entry. The combination of profileId, folderId, type, and objectId must be unique.");
            } else {
                throw new ConflictException("Can't add Favourite because of not found profileId or folderId");
            }
        } catch (Exception e) {
            log.error("Unexpected error occurred while adding favourite. objectId: {}, folderId: {}", objectId, folderId);
            throw new ConflictException("Can't add Favourite due to an unexpected error");
        }
    }

    private float findMaxOrder(Long folderId) {
        float maxOrder = favouriteRepository.findMaxOrder(folderId).orElse(0f);
        float order;
        if (Math.ceil(maxOrder) == maxOrder) {
            order = maxOrder + 1;
        } else {
            order = (float) Math.ceil(maxOrder);
        }
        return order;
    }

    private boolean containsConstraintViolation(Throwable e, String constraintName) {
        if (e == null) {
            return false;
        }
        if (e instanceof SQLException && e.getMessage().contains(constraintName)) {
            return true;
        }
        return containsConstraintViolation(e.getCause(), constraintName);
    }

    @Transactional
    public void deleteFavourite(String objectId, Long profileId, FavouriteType type, Long folderId) throws ConflictException {

        log.info("Start processing: deleting OBJECT (id: {}, type: {}) from Favourites. Folder: '{}'. USER: '{}'", objectId, type, folderId, profileId);

        if (!favouriteExists(objectId, profileId, type, folderId)) {
            log.warn("Favourite not found for deletion. USER: '{}', OBJECT id: {}, type: {}, Folder: '{}'", profileId, objectId, type, folderId);
            throw new ConflictException(generateConflictMessage(folderId));
        }

        if (folderId != null) {
            log.info("Deleting OBJECT (id: {}, type: {}) via !PERSONAL SPACE!. Folder: '{}'. USER: '{}'", objectId, type, folderId, profileId);
            favouriteRepository.deleteFromFavourites(objectId, type, folderId);
        } else {
            log.info("Deleting OBJECT (id: {}, type: {}) via !MAIN APP! from all folders. USER: '{}'", objectId, type, profileId);
            favouriteRepository.deleteFromFavourites(objectId, profileId, type);
        }

        deleteUnusedBackup(objectId, type);

        log.info("Successfully deleted OBJECT (id: {}, type: {}). Folder: '{}'. USER: '{}'", objectId, type, folderId, profileId);
    }

    private void deleteUnusedBackup(String objectId, FavouriteType type) {
        if (!favouriteRepository.existsByObjectIdAndType(objectId, type)) {
            favouriteBackupRepository.deleteByObjectIdAndType(objectId, type);
        }
    }

    @Transactional
    public void move(@NonNull FavouriteDto object,
                     @NonNull Long fromFolderId, Long toFolderId,
                     Integer fromPageNumber, Integer toPageNumber,
                     FavouriteDto prevObject, FavouriteDto nextObject) {
        if (toPageNumber != null) {
            moveToPage(object, fromFolderId, fromPageNumber, toPageNumber);
        } else if (prevObject != null || nextObject != null) {
            moveOnPage(object, fromFolderId, prevObject, nextObject);
        } else if (toFolderId != null && !toFolderId.equals(fromFolderId)) {
            moveToFolder(object, fromFolderId, toFolderId);
        }
    }

    private void moveToPage(FavouriteDto object, Long folderId, Integer fromPageNumber, Integer toPageNumber) {

        int objectsShift = (toPageNumber != 0 && toPageNumber < fromPageNumber) ? -1 : 0;
        int firstResult = toPageNumber * smbPersonalSpace.getPageSize() + objectsShift;

        List<Float> insertBetweenOrders = moveFavouritesRepository.findInsertBetweenOrders(folderId, firstResult);

        float updatedOrder = (insertBetweenOrders.size() == 1) ? (insertBetweenOrders.get(0) + 1) : ((insertBetweenOrders.get(0) + insertBetweenOrders.get(1)) / 2);
        favouriteRepository.updateOrder(updatedOrder, object.getObjectId(), object.getType(), folderId);
    }

    /**
     * @param prevObject - null, если object в начале списка или страницы
     * @param nextObject - null, если object в конце списка или страницы
     */
    private void moveOnPage(FavouriteDto object, Long folderId,
                            FavouriteDto prevObject, FavouriteDto nextObject) {

        if (prevObject == null && nextObject == null || folderId == null) {
            // TODO throw exception
            return;
        }

        Float prevObjectOrder;
        if (prevObject != null) {
            prevObjectOrder = favouriteRepository.findOrderByObjectId(prevObject.getObjectId(), prevObject.getType(), folderId).orElseThrow(null);
        } else {
            prevObjectOrder = favouriteRepository.findPrevOrder(nextObject.getObjectId(), nextObject.getType(), folderId).orElse(null);
        }

        Float nextObjectOrder;
        if (nextObject != null) {
            nextObjectOrder = favouriteRepository.findOrderByObjectId(nextObject.getObjectId(), nextObject.getType(), folderId).orElseThrow(null);
        } else {
            nextObjectOrder = favouriteRepository.findNextOrder(prevObject.getObjectId(), prevObject.getType(), folderId).orElse(null);
        }

        float updatedOrder;
        if (prevObjectOrder != null && nextObjectOrder != null) {
            updatedOrder = (prevObjectOrder + nextObjectOrder) / 2;
        } else if (prevObjectOrder != null) {
            updatedOrder = prevObjectOrder - 1;
        } else if (nextObjectOrder != null) {
            updatedOrder = nextObjectOrder + 1;
        } else {
            // TODO throw exception
            return;
        }

        favouriteRepository.updateOrder(updatedOrder, object.getObjectId(), object.getType(), folderId);
    }

    private void moveToFolder(FavouriteDto object, Long fromFolderId, Long toFolderId) {
        if (!favouriteRepository.findByObjectIdAndTypeAndFolderId(object.getObjectId(), object.getType(), toFolderId).isPresent()) {
            favouriteRepository.updateFolderId(toFolderId, findMaxOrder(toFolderId), object.getObjectId(), fromFolderId);
        } else {
            favouriteRepository.deleteFromFavourites(object.getObjectId(), object.getType(), fromFolderId);
        }
    }

    private boolean favouriteExists(String objectId, Long profileId, FavouriteType type, Long folderId) {
        if (folderId != null) {
            return favouriteRepository.existsByObjectIdAndProfileIdAndTypeAndFolderId(objectId, profileId, type, folderId);
        } else {
            return favouriteRepository.existsByObjectIdAndProfileIdAndType(objectId, profileId, type);
        }
    }

    private String generateConflictMessage(Long folderId) {
        if (folderId != null) {
            return "Can't delete favourite entry: no entry found with the specified objectId, profileId, type, and folderId.";
        } else {
            return "Can't delete favourite entry: no entry found with the specified objectId, profileId, and type.";
        }
    }

    public Boolean getFavouriteArticle(Long itemId, Long profileId) {
        // TODO хуйня, зачем идет поиск всех групп? Какой-то кринж вместо exists
        if (profileRepository.findProfileInGroups(profileId, smbPersonalSpaceProperties.getAvailableGroups()).isEmpty()) {
            return null;
        }
        return !favouriteRepository.findFavouriteArticlesIds(Long.toString(itemId), profileId).isEmpty();
    }

    public List<String> checkFavouritesByStringIds(List<String> objectIds, Long profileId, FavouriteType type) {
        return favouriteRepository.checkFavourites(objectIds, profileId, type);
    }

    public List<Long> checkFavouritesByLongIds(List<Long> objectIds, Long profileId, FavouriteType type) {
        return favouriteRepository
                .checkFavourites(objectIds.stream().map(Object::toString).collect(Collectors.toList()), profileId, type)
                .stream().map(Long::parseLong).collect(Collectors.toList());
    }

    public List<String> findFavouritesStringIds(Long profileId, FavouriteType type) {
        return favouriteRepository.findFavouritesIds(profileId, type);
    }

    public List<Long> findFavouritesLongIds(Long profileId, FavouriteType type) {
        return favouriteRepository.findFavouritesIds(profileId, type).stream().map(Long::parseLong).collect(Collectors.toList());
    }
}