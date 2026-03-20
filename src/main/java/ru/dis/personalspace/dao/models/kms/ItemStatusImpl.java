package ru.dis.personalspace.dao.models.kms;

import javax.persistence.Entity;
import javax.persistence.Table;

import ru.disgroup.kms.common.core.domain.model.kms.ItemStatus;

@Entity
@Table(name = ItemStatus.TABLE_NAME)
public class ItemStatusImpl extends ItemStatus{

}
