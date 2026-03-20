package ru.dis.personalspace.dao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dis.personalspace.enums.FavouriteType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavouriteRsDto {
    private Object object;
    private FavouriteType type;
    private String status;

    public FavouriteRsDto(Object object, FavouriteType type) {
        this.object = object;
        this.type = type;
        this.status = null;
    }
}
