package ru.dis.personalspace.dao.dto.response;

import java.util.List;

import lombok.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FavouritesPageRsDto<T> extends PageImpl<T> {

    private List<Long> folders;

    public FavouritesPageRsDto(List<T> content, Pageable pageable, long total,
                               List<Long> folders) {
        super(content, pageable, total);
        this.folders = folders;
    }

    public FavouritesPageRsDto(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
