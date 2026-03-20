package ru.dis.personalspace.dao.dto.normDoc;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormDocDto {
    private Long docId;
    private String docDescription;
    private String status;
    private List<NormDocArticleDto> relatedArticles;
}
