package ru.dis.personalspace.dao.models;

import lombok.*;
import ru.dis.personalspace.enums.FavouriteType;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Table(name = "DIS_PERSONAL_SPACE_OBJECTS_BACKUP")
public class PersonalSpaceFavouriteBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NonNull
    @Column(name = "OBJECT_ID")
    private String objectId;

    @NonNull
    @Column(name = "TITLE")
    private String title;

    @Column(name = "ADDITIONAL_INFORMATION")
    private String additionalInformation;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private FavouriteType type;

}
