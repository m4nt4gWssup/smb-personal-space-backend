package ru.dis.personalspace.dao.models.kms;

import javax.persistence.Entity;
import javax.persistence.Table;
import ru.disgroup.kms.common.core.domain.model.kms.ItemType;

@Entity
@Table(name = ItemType.TABLE_NAME)
public class ItemTypeImpl extends ItemType {

}