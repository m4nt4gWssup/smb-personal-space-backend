package ru.dis.personalspace.dao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.dis.personalspace.enums.FavouriteType;

@Data
@AllArgsConstructor
public class FavouriteDto {
    private String objectId;
    private FavouriteType type;
}
