package ru.dis.personalspace.factory.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.dis.personalspace.config.properties.SmbPersonalSpaceProperties;
import ru.dis.personalspace.dao.dto.ArticleDto;
import ru.dis.personalspace.dao.dto.response.FavouriteRsDto;
import ru.dis.personalspace.dao.repository.kms.ItemTypeRepositoryImpl;
import ru.dis.personalspace.enums.FavouriteType;
import ru.dis.personalspace.factory.FavouritesProcessor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ArticleProcessor implements FavouritesProcessor {
    private final SmbPersonalSpaceProperties.SmbPersonalSpace smbPersonalSpace;
    private final ItemTypeRepositoryImpl itemTypeRepository;

    @Override
    public List<FavouriteRsDto> findObjects(Long profileId, Collection<String> objectIds) {
        return findArticles(objectIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList()));
    }

    private List<FavouriteRsDto> findArticles(List<Long> ids) {
        return itemTypeRepository.findArticles(ids).stream()
                .map(article -> new FavouriteRsDto(article, FavouriteType.ARTICLE))
                .collect(Collectors.toList());
    }

    @Override
    public String getObjectId(FavouriteRsDto dto) {
        return ((ArticleDto) dto.getObject()).getItemId().toString();
    }

    @Override
    public void processLink(FavouriteRsDto dto) {
        ArticleDto article = (ArticleDto) dto.getObject();
        article.setLink(smbPersonalSpace.getArticleLink().replace("<ITEM_ID>", article.getItemId().toString()));
    }
}
