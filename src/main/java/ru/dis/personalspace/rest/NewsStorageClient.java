package ru.dis.personalspace.rest;

import java.util.List;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import ru.dis.personalspace.dao.dto.NewsDto;

@FeignClient(value = "news-storage-client", url = "${ru.disgroup.smb-personal-space.news-storage-url}")
public interface NewsStorageClient {
    @GetMapping(value = "/news/filter/favourites",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            headers = {"Content-Type: application/json"})
    List<NewsDto> findNews(@RequestHeader("profileId") Long profileId,
                           @RequestParam("newsIds") Set<Long> newsIds);
}
