package ru.dis.personalspace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.dis.personalspace.config.properties.SmbPersonalSpaceProperties;
import ru.dis.personalspace.dao.dto.response.FavouriteRsDto;
import ru.dis.personalspace.dao.dto.response.FavouritesPageRsDto;
import ru.dis.personalspace.enums.FavouriteType;
import ru.dis.personalspace.service.FindFavouritesService;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/favourites/find")
@Tag(name = "Find Favourites API")
public class FindFavouritesController {
    private final FindFavouritesService findFavouritesService;
    private final SmbPersonalSpaceProperties.SmbPersonalSpace smbPersonalSpace;

    @GetMapping(produces = "application/json;charset=utf-8")
    @Operation(summary = "Check if objects from list are Favourite")
    public ResponseEntity<FavouritesPageRsDto<FavouriteRsDto>> findFavourites(
            @RequestHeader(value = "profileId") Long profileId,
            @RequestParam(name = "folderId", required = false) Long folderId,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", required = false) Integer pageSize) {
        if (pageSize == null) {
            pageSize = smbPersonalSpace.getPageSize();
        }
        return ResponseEntity.ok(findFavouritesService.findFavourites(profileId, folderId, null, PageRequest.of(pageNumber, pageSize)));
    }

    @GetMapping(value = "/{type}", produces = "application/json;charset=utf-8")
    @Operation(summary = "Check if objects from list are Favourite")
    public ResponseEntity<FavouritesPageRsDto<FavouriteRsDto>> findFavourites(
            @RequestHeader(value = "profileId") Long profileId,
            @RequestParam(name = "folderId", required = false) Long folderId,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @PathVariable(name = "type") FavouriteType type) {
        if (pageSize == null) {
            pageSize = smbPersonalSpace.getPageSize();
        }
        return ResponseEntity.ok(findFavouritesService.findFavourites(profileId, folderId, type, PageRequest.of(pageNumber, pageSize)));
    }
}
