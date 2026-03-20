package ru.dis.personalspace.dao.models.kms;

import ru.disgroup.kms.common.core.domain.model.kms.Profile;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = Profile.TABLE_NAME)
public class ProfileImpl extends Profile {

}