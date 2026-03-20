package ru.dis.personalspace.dao.dto.request.folders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class DeleteFolderRqDto {
    @NonNull
    private Long folderId;
}
