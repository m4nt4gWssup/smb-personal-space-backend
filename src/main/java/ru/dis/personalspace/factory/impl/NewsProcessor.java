package ru.dis.personalspace.factory.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.dis.personalspace.dao.dto.NewsDto;
import ru.dis.personalspace.enums.FavouriteType;
import ru.dis.personalspace.factory.FavouritesProcessor;
import ru.dis.personalspace.dao.dto.response.FavouriteRsDto;
import ru.dis.personalspace.rest.NewsStorageClient;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NewsProcessor implements FavouritesProcessor {
    private final NewsStorageClient newsStorageClient;

    @Override
    public List<FavouriteRsDto> findObjects(Long profileId, Collection<String> objectIds) {
        return findNews(profileId, objectIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toSet()));
    }

    @Override
    public String getObjectId(FavouriteRsDto dto) {
        return ((NewsDto) dto.getObject()).getId().toString();
    }

    private List<FavouriteRsDto> findNews(Long profileId, Set<Long> newsIds) {
        return newsStorageClient.findNews(profileId, newsIds).stream()
                .map(news -> new FavouriteRsDto(news, FavouriteType.NEWS))
                .collect(Collectors.toList());
    }
}
