package ru.dis.personalspace.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ArticleDto {
    private static final String ARCHIVE = "Архивная";
    private static final String DELETED = "Удалена";
    private static final String DELETED_STATE = "D";

    private Long itemId;
    private String name;
    private String status;
    private String link;

    public ArticleDto(Long itemId, String name, Integer status, String state, Integer deleted) {
        this.itemId = itemId;
        this.name = name;

        if (status != null && !DELETED_STATE.equals(state) && deleted != 1) {
            if (status != 2) {
                this.status = status == 4 ? ARCHIVE : DELETED;
            }
        } else {
            this.status = DELETED;
        }
    }
}
