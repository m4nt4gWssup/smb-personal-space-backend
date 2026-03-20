package ru.dis.personalspace.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderTreeNode {

    private Long folderId;

    private String title;

    @Builder.Default
    private List<FolderTreeNode> children = new ArrayList<>();

    private Long parentId;
}
