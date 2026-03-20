package ru.dis.personalspace.factory.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.dis.personalspace.dao.dto.FaqDocumentDto;
import ru.dis.personalspace.dao.models.PersonalSpaceFavouriteBackup;
import ru.dis.personalspace.enums.FavouriteType;
import ru.dis.personalspace.factory.FavouritesProcessor;
import lombok.RequiredArgsConstructor;
import ru.dis.personalspace.dao.dto.response.FavouriteRsDto;
import ru.dis.personalspace.rest.FaqClient;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FaqProcessor implements FavouritesProcessor {
    private final FaqClient faqClient;

    @Override
    public List<FavouriteRsDto> findObjects(Long profileId, Collection<String> objectIds) {
        return findFaqs((List<String>) objectIds);
    }

    private List<FavouriteRsDto> findFaqs(List<String> docNumbers) {
        return faqClient.finFaqs(docNumbers).stream()
                .map(faq -> new FavouriteRsDto(faq, FavouriteType.FAQ))
                .collect(Collectors.toList());
    }

    @Override
    public String getObjectId(FavouriteRsDto dto) {
        return ((FaqDocumentDto) dto.getObject()).getDocNumber();
    }

    @Override
    public FavouriteRsDto createFavouriteFromBackup(PersonalSpaceFavouriteBackup backup) {
        String[] groups = StringUtils.split(backup.getAdditionalInformation(), ",");
        return new FavouriteRsDto(FaqDocumentDto.builder()
                .docNumber(backup.getObjectId())
                .problem(backup.getTitle())
                .groups(groups != null ? Arrays.asList(groups) : null)
                .build(), FavouriteType.FAQ, DELETED);
    }

    @Override
    public String getAdditionalInformation(String objectId) {
        return String.join(",", faqClient.findFaqGroups(objectId));
    }
}
