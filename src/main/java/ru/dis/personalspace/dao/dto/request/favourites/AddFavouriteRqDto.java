package ru.dis.personalspace.dao.dto.request.favourites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.dis.personalspace.enums.FavouriteType;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class AddFavouriteRqDto {
    @NonNull
    private String objectId;
    private String title;
    @NonNull
    private FavouriteType type;
    @NonNull
    private Long folderId;
}
