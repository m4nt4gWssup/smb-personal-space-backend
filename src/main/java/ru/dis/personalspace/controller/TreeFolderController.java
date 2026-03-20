package ru.dis.personalspace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.dis.personalspace.dao.dto.FolderTreeNode;
import ru.dis.personalspace.dao.dto.request.folders.CreateFolderRqDto;
import ru.dis.personalspace.dao.dto.request.folders.MoveFolderRqDto;
import ru.dis.personalspace.dao.dto.request.folders.RenameFolderRqDto;
import ru.dis.personalspace.service.TreeFolderService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/folders")
@Tag(name = "Folder tree API")
public class TreeFolderController {

    private final TreeFolderService treeFolderService;

    @PostMapping(value = "/create", produces = "application/json;charset=utf-8")
    @Operation(summary = "Create new folder")
    public ResponseEntity<FolderTreeNode> createFolder(@RequestHeader(value = "profileid", required = true) Long profileId,
            @RequestBody CreateFolderRqDto rqDto) {
        return ResponseEntity.ok(treeFolderService.createFolder(rqDto.getName(), profileId, rqDto.getParentId()));
    }

    @PatchMapping(value = "/rename", produces = "application/json;charset=utf-8")
    @Operation(summary = "Rename folder")
    public ResponseEntity<FolderTreeNode> renameFolder(@RequestBody RenameFolderRqDto rqDto) {
        return ResponseEntity.ok(treeFolderService.renameFolder(rqDto.getFolderId(), rqDto.getName()));
    }

    @DeleteMapping(value = "/delete", produces = "application/json;charset=utf-8")
    @Operation(summary = "Delete folder")
    public ResponseEntity<Void> deleteFolder(@RequestParam("folderId") Long folderId) {
        treeFolderService.deleteFolder(folderId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/move", produces = "application/json;charset=utf-8")
    @Operation(summary = "Move folder")
    public ResponseEntity<FolderTreeNode> move(@RequestBody MoveFolderRqDto rqDto) {
        treeFolderService.moveFolder(rqDto.getFolderId(),
                rqDto.getPrevFolderId(), rqDto.getNextFolderId(),
                rqDto.getFromParentId(), rqDto.getToParentId());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/get-tree", produces = "application/json;charset=utf-8")
    @Operation(summary = "Get folder tree")
    public ResponseEntity<List<FolderTreeNode>> getFolderTree(
            @RequestHeader(value = "profileId", required = true) Long profileId) throws JsonProcessingException {
        treeFolderService.createFavouritesFolder(profileId);
        return ResponseEntity.ok(treeFolderService.getFolderTree(profileId));
    }
}