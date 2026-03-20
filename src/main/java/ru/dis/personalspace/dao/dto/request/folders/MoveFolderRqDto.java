package ru.dis.personalspace.dao.dto.request.folders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class MoveFolderRqDto {
    @NonNull
    private Long folderId;
    private Long prevFolderId;
    private Long nextFolderId;
    private Long fromParentId;
    private Long toParentId;
}
