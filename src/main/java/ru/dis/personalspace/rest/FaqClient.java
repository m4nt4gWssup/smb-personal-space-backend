package ru.dis.personalspace.rest;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.dis.personalspace.dao.dto.FaqDocumentDto;

@FeignClient(value = "faq-client", url = "${ru.disgroup.smb-personal-space.faq-url}")
public interface FaqClient {
    @GetMapping(value = "/faq/storage/favourites",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            headers = {"Content-Type: application/json"})
    List<FaqDocumentDto> finFaqs(@RequestParam("docNumbers") List<String> docNumbers);

    @GetMapping(value = "/faq/storage/groups",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            headers = {"Content-Type: application/json"})
    List<String> findFaqGroups(@RequestParam("docNumber") String docNumber);
}
