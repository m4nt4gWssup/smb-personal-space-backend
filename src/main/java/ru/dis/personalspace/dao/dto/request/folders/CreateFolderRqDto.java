package ru.dis.personalspace.dao.dto.request.folders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CreateFolderRqDto {
    @NonNull
    private String name;
    private Long parentId;
}
