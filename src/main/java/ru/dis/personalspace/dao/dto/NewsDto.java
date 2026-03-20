package ru.dis.personalspace.dao.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class NewsDto {
    private Long id;
    private String name;
    private Long itemId;
    private String shortName;
    private String shortTitle;
    private String content;
    private String cm;
    private List<String> groups;
    private Boolean checkEmail;
    private LocalDateTime date;
    private String readStatus;
    private LocalDateTime lastEmail;
    private Boolean userReadStatus;
    private Boolean userViewed;
    private String oschs;
    private Boolean isDeleted;
    private Boolean isPinned;
    private Boolean isImportance;
    private Boolean isVisualRatingShow;
    private Boolean isNewsComments;
    private String status;
    private String link;
}
