package ru.dis.personalspace.rest;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.dis.personalspace.dao.dto.normDoc.NormDocDto;

@FeignClient(value = "norm-docs-client", url = "${ru.disgroup.smb-personal-space.norm-docs-url}")
public interface NormDocsClient {
    @GetMapping(value = "/getDocsByIds",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            headers = {"Content-Type: application/json"})
    List<NormDocDto> findNormDocs(@RequestParam("ids") List<Long> ids);
}
