package ru.dis.personalspace.dao.models;

import lombok.*;
import ru.dis.personalspace.enums.FavouriteType;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Table(name = "DIS_PERSONAL_SPACE")
public class PersonalSpaceFavourite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NonNull
    @Column(name = "OBJECT_ID")
    private String objectId;

    @NonNull
    @Column(name = "PROFILE_ID")
    private Long profileId;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private FavouriteType type;

    @NonNull
    @Column(name = "FOLDER_ID")
    private Long folderId;

    @NonNull
    @Column(name = "SORT_ORDER")
    private Float order;

    @CreationTimestamp
    @Column(name = "CREATION_TIMESTAMP")
    private LocalDateTime creationDateTime;
}
