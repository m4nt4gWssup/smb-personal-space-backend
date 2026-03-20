package ru.dis.personalspace.dao.dto.normDoc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NormDocArticleDto {
    private Long articleId;
    private String name;
    private String link;
}
