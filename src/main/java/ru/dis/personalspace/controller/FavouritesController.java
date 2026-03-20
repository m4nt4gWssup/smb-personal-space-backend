package ru.dis.personalspace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.dis.personalspace.dao.dto.FolderTreeNode;
import ru.dis.personalspace.dao.dto.request.favourites.AddFavouriteRqDto;
import ru.dis.personalspace.dao.dto.request.favourites.MoveFavouriteRqDto;
import ru.dis.personalspace.enums.FavouriteType;
import ru.dis.personalspace.service.FavouritesService;
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

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/favourites")
@Tag(name = "Favourites API")
public class FavouritesController {
    private final FavouritesService favouritesService;

    @PostMapping(value = "/add", produces = "application/json;charset=utf-8")
    @Operation(summary = "Add new object to Favourites")
    public ResponseEntity<Void> addFavourite(@RequestHeader(value = "profileId") Long profileId,
                                             @RequestBody AddFavouriteRqDto rqDto) {
        favouritesService.addAndBackupFavourite(rqDto.getObjectId(), rqDto.getTitle(), profileId, rqDto.getType(), rqDto.getFolderId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/delete", produces = "application/json;charset=utf-8")
    @Operation(summary = "Delete object from Favourites")
    public ResponseEntity<Void> deleteFromFavourites(
            @RequestHeader(value = "profileId") Long profileId,
            @RequestParam(name = "objectId") String objectId,
            @RequestParam(name = "type") FavouriteType type,
            @RequestParam(name = "folderId", required = false) Long folderId) {
        favouritesService.deleteFavourite(objectId, profileId, type, folderId);
        return ResponseEntity.ok().build();
    }
        
    @PatchMapping(value = "/move", produces = "application/json;charset=utf-8" )
    @Operation(summary = "Move object")
    public ResponseEntity<FolderTreeNode> move(@RequestBody MoveFavouriteRqDto rqDto) {
        favouritesService.move(rqDto.getObject(), rqDto.getFromFolderId(),rqDto.getToFolderId(),rqDto.getFromPageNumber(), rqDto.getToPageNumber(), rqDto.getPrevObject(),rqDto.getNextObject());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/is-favourite-article", produces = "application/json;charset=utf-8")
    @Operation(summary = "Check if article with itemId is Favourite")
    public ResponseEntity<Boolean> isFavouriteArticle(
            @RequestHeader(value = "profileId") Long profileId,
            @RequestParam(name = "itemId") Long itemId) {
        return ResponseEntity
                .ok(favouritesService.getFavouriteArticle(itemId, profileId));
    }

    @GetMapping(value = "/check-by-long-ids", produces = "application/json;charset=utf-8")
    @Operation(summary = "Check if objects from list are Favourite")
    public ResponseEntity<List<Long>> checkFavouritesByLongIds(
            @RequestHeader(value = "profileId") Long profileId,
            @RequestParam(name = "objectIds") List<Long> objectIds,
            @RequestParam(name = "type") FavouriteType type) {
        return ResponseEntity
                .ok(favouritesService.checkFavouritesByLongIds(objectIds, profileId, type));
    }

    @GetMapping(value = "/check-by-string-ids", produces = "application/json;charset=utf-8")
    @Operation(summary = "Check if objects from list are Favourite")
    public ResponseEntity<List<String>> checkFavouritesByStringIds(
            @RequestHeader(value = "profileId") Long profileId,
            @RequestParam(name = "objectIds") List<String> objectIds,
            @RequestParam(name = "type") FavouriteType type) {
        return ResponseEntity
                .ok(favouritesService.checkFavouritesByStringIds(objectIds, profileId, type));
    }

    @GetMapping(value = "/find-favourites-string-ids", produces = "application/json;charset=utf-8")
    @Operation(summary = "Check if objects from list are Favourite")
    public ResponseEntity<List<String>> findFavouritesStringIds(
            @RequestHeader(value = "profileId") Long profileId,
            @RequestParam(name = "type") FavouriteType type) {
        return ResponseEntity
                .ok(favouritesService.findFavouritesStringIds(profileId, type));
    }

    @GetMapping(value = "/find-favourites-long-ids", produces = "application/json;charset=utf-8")
    @Operation(summary = "Check if objects from list are Favourite")
    public ResponseEntity<List<Long>> findFavouritesLongIds(
            @RequestHeader(value = "profileId") Long profileId,
            @RequestParam(name = "type") FavouriteType type) {
        return ResponseEntity
                .ok(favouritesService.findFavouritesLongIds(profileId, type));
    }
}