package ru.dis.personalspace.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.dis.personalspace.dao.dto.FolderTreeNode;
import ru.dis.personalspace.dao.dto.Projector;
import ru.dis.personalspace.dao.models.PersonalSpaceFavourite;
import ru.dis.personalspace.dao.models.PersonalSpaceFolder;
import ru.dis.personalspace.dao.repository.PersonalSpaceFavouriteRepository;
import ru.dis.personalspace.dao.repository.PersonalSpaceFolderRepository;
import ru.dis.personalspace.dao.repository.kms.ProfileRepositoryImpl;
import ru.dis.personalspace.util.PersonalSpaceUtils;
import ru.disgroup.kms.common.core.domain.exceptions.response.ConflictException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TreeFolderService {
    private final PersonalSpaceFolderRepository folderRepository;
    private final PersonalSpaceFavouriteRepository favouriteRepository;
    private static final String FAVOURITES_FOLDER_NAME = "Избранное";
    private static final float MIN_ORDER = 1;

    @Transactional
    public FolderTreeNode createFolder(String name, Long profileId, Long parentId) throws ConflictException {
        PersonalSpaceFolder folder = folderRepository.save(PersonalSpaceFolder.builder()
                .name(name)
                .parentFolder(findParentFolder(parentId))
                .profileId(profileId)
                .order(findFolderOrder(profileId, parentId)).build());

        return FolderTreeNode.builder()
                .folderId(folder.getId())
                .title(folder.getName())
                .parentId(parentId)
                .build();
    }

    private float findFolderOrder(Long profileId, Long parentId) {

        float prevOrder;
        if (parentId != null) {
            prevOrder = folderRepository.findMaxOrder(profileId, parentId).orElse(0f);
        } else {
            prevOrder = folderRepository.findMaxOrder(profileId).orElse(0f);
        }

        if (prevOrder == 0) {
            return MIN_ORDER;
        }

        float ceiledPrevOrder = (float) Math.ceil(prevOrder);
        if (prevOrder == ceiledPrevOrder) {
            return prevOrder + 1;
        } else {
            return ceiledPrevOrder;
        }
    }

    private PersonalSpaceFolder findParentFolder(Long parentId) throws ConflictException {
        if (parentId != null) {
            return folderRepository.findById(parentId)
                    .orElseThrow(() -> new ConflictException("Can't create folder because of not found profileId or parentId"));
        }
        return null;
    }

    @Transactional
    public FolderTreeNode renameFolder(Long folderId, String name) throws ConflictException {
        PersonalSpaceFolder personalSpaceFolder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ConflictException("Folder with id " + folderId + " not found"));
        personalSpaceFolder.setName(name);
        PersonalSpaceFolder folder = folderRepository.save(personalSpaceFolder);
        return FolderTreeNode.builder()
                .folderId(folder.getId())
                .title(folder.getName())
                .parentId(folder.getParentFolder() == null ? null : folder.getParentFolder().getId())
                .build();
    }

    @Transactional
    public void deleteFolder(Long folderId) throws ConflictException {
        try {
            List<PersonalSpaceFavourite> folderObjects = favouriteRepository.findAllByFolderId(folderId);
            if (!folderObjects.isEmpty()) {
                String objectsLog = folderObjects.stream()
                        .map(fav -> String.format("id: %s, type: %s", fav.getObjectId(), fav.getType()))
                        .collect(Collectors.joining("; "));

                log.info("Deleting OBJECTS: \n" + "   [{}]\n" + "via PERSONAL SPACE from folder (id: {}).",
                        objectsLog, folderId);
            }

            folderRepository.deleteById(folderId);

            log.info("Successfully deleted FOLDER (id: {}).", folderId);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Folder with id - {} not found", folderId);
            throw new ConflictException("Folder with id " + folderId + " not found");
        }
    }

    @Transactional
    public void moveFolder(Long folderId,
                           Long prevFolderId, Long nextFolderId,
                           Long fromParentId, Long toParentId) {
        if (toParentId.equals(fromParentId)) {
            toParentId = null;
        }

        if (prevFolderId == null && nextFolderId == null && toParentId == null) {
            // TODO throw exception
            return;
        }

        if (toParentId != null) {
            reparentFolder(folderId, toParentId);
        }
        if (prevFolderId != null || nextFolderId != null) {
            reorderFolder(folderId, prevFolderId, nextFolderId);
        }
    }

    private void reorderFolder(Long folderId,
                               Long prevFolderId, Long nextFolderId){

        Float prevFolderOrder = null;
        if (prevFolderId != null) {
            prevFolderOrder = folderRepository.findOrderByFolderId(prevFolderId).orElseThrow(null);
        }

        Float nextFolderOrder = null;
        if (nextFolderId != null) {
            nextFolderOrder = folderRepository.findOrderByFolderId(nextFolderId).orElseThrow(null);
        }

        float updatedOrder;
        if (prevFolderOrder != null && nextFolderOrder != null) {
            updatedOrder = (prevFolderOrder + nextFolderOrder) / 2;
        } else if (prevFolderOrder != null) {
            updatedOrder = prevFolderOrder + 1;
        } else if (nextFolderOrder != null) {
            updatedOrder = nextFolderOrder - 1;
        } else {
            // TODO throw exception
            return;
        }

        folderRepository.updateOrder(folderId, updatedOrder);
    }

    private void reparentFolder(Long folderId, Long toParentId) {
        PersonalSpaceFolder parentFolder;
        if (toParentId != 0) {
            parentFolder = folderRepository.findById(toParentId).orElseThrow(null);
        } else {
            parentFolder = null;
        }

        folderRepository.updateParentFolder(folderId, parentFolder);
    }

    @Transactional
    public List<FolderTreeNode> getFolderTree(Long profileId) {
        List<FolderTreeNode> root = new ArrayList<>();

        List<Projector> dtos = folderRepository.getTree(profileId);
        for (Projector dto : dtos) {
            String[] paths = dto.getFolderNames().split("%!%");
            String[] folderIds = dto.getFolderIds().split("%!%");
            log.debug("Extracted folderNames: {} with ids: {} ", paths, folderIds);
            path(root, paths, folderIds, 0);
        }
        log.debug("Completed building folder tree for profileId: {}. Final root: {}", profileId, root);
        return root;
    }

    private void path(List<FolderTreeNode> node, String[] paths, String[] folderIds, int lvl) {
        if (paths.length > lvl) {
            int index = -1;
            for (int i = 0; i < node.size(); i++) {
                if (node.get(i).getTitle().equals(paths[lvl])) {
                    index = i;
                }
            }
            if (index >= 0) {
                node = node.get(index).getChildren();
            } else {
                node.add(new FolderTreeNode(Long.parseLong(folderIds[lvl]), paths[lvl], new ArrayList<>(),
                        getParentId(folderIds, lvl)));
                node = node.get(node.size() - 1).getChildren();
            }

            path(node, paths, folderIds, lvl + 1);
        }
    }

    private Long getParentId(String[] folderIds, int lvl) {
        if (lvl > 0)
            return Long.parseLong(folderIds[lvl - 1]);
        return null;
    }

    public void createFavouritesFolder(Long profileId) {
        if (!folderRepository.findFavouritesFolder(profileId).isPresent()) {
            folderRepository.save(PersonalSpaceFolder.builder()
                    .name(FAVOURITES_FOLDER_NAME)
                    .profileId(profileId)
                    .order(MIN_ORDER).build());
        }
    }
}