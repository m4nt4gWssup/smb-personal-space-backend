package ru.dis.personalspace.dao.repository.kms;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ru.dis.personalspace.dao.dto.ArticleDto;
import ru.dis.personalspace.dao.models.kms.ItemTypeImpl;
import ru.disgroup.kms.common.core.domain.dao.kms.ItemTypeRepository;

public interface ItemTypeRepositoryImpl extends ItemTypeRepository<ItemTypeImpl> {
    @Query(value = "select new ru.dis.personalspace.dao.dto.ArticleDto(it.itemId as itemId, it.title as name, ise.status as status, it.state as state, it.deleted as deleted) from ItemTypeImpl it " +
            " left join ItemStatusImpl ise on it.itemId = ise.itemId and current_timestamp BETWEEN ise.fromDate AND COALESCE(ise.toDate, current_timestamp)" +
            " where it.itemId in :articleIds")
    List<ArticleDto> findArticles(List<Long> articleIds);
}
