package ru.dis.personalspace.dao.dto.request.favourites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.dis.personalspace.dao.dto.FavouriteDto;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class MoveFavouriteRqDto {
    @NonNull
    private FavouriteDto object;
    @NonNull
    private Long fromFolderId;
    private Long toFolderId;
    private Integer fromPageNumber;
    private Integer toPageNumber;
    private FavouriteDto prevObject;
    private FavouriteDto nextObject;
}
