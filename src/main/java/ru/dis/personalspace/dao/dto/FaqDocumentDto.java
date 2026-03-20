package ru.dis.personalspace.dao.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FaqDocumentDto {
    private Long id;
    private String docNumber;
    private String docType;
    private String docTitle;
    private String docStatus;
    private LocalDateTime docLastUpdate;
    private LocalDateTime docExpiration;
    private String authorName;
    private String authorEmail;
    private String problem;
    private String solution;
    private Boolean isManual;
    private List<String> groups;
}