package ru.dis.personalspace.factory.impl;

import org.springframework.stereotype.Component;
import ru.dis.personalspace.dao.dto.normDoc.NormDocDto;
import ru.dis.personalspace.dao.models.PersonalSpaceFavouriteBackup;
import ru.dis.personalspace.enums.FavouriteType;
import ru.dis.personalspace.factory.FavouritesProcessor;
import lombok.RequiredArgsConstructor;
import ru.dis.personalspace.dao.dto.response.FavouriteRsDto;
import ru.dis.personalspace.rest.NormDocsClient;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NormDocProcessor implements FavouritesProcessor {
    private final NormDocsClient normDocsClient;

    @Override
    public List<FavouriteRsDto> findObjects(Long profileId, Collection<String> objectIds) {
        return findNormDocs(objectIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList()));
    }

    private List<FavouriteRsDto> findNormDocs(List<Long> normDocsIds) {
        return normDocsClient.findNormDocs(normDocsIds).stream()
                .map(normDoc -> new FavouriteRsDto(normDoc, FavouriteType.NORM_DOC))
                .collect(Collectors.toList());
    }

    @Override
    public String getObjectId(FavouriteRsDto dto) {
        return ((NormDocDto) dto.getObject()).getDocId().toString();
    }

    @Override
    public FavouriteRsDto createFavouriteFromBackup(PersonalSpaceFavouriteBackup backup) {
        return new FavouriteRsDto(NormDocDto.builder()
                .docId(Long.parseLong(backup.getObjectId()))
                .docDescription(backup.getTitle())
                .build(), FavouriteType.NORM_DOC, DELETED);
    }
}
