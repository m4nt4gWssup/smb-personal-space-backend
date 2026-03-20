package ru.dis.personalspace.dao.models;

import lombok.*;

import java.util.List;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "DIS_PERSONAL_SPACE_FOLDERS")
public class PersonalSpaceFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOLDER_ID")
    private Long id;

    @NonNull
    @Column(name = "FOLDER_NAME")
    private String name;
    
    @ManyToOne
    @JsonIgnoreProperties
    @ToStringExclude
    @JoinColumn(name="PARENT_ID")
    private PersonalSpaceFolder parentFolder;

    @NonNull
    @Column(name = "PROFILE_ID")
    private Long profileId;

    @NonNull
    @Column(name = "FOLDER_ORDER")
    private Float order;

    @OneToMany(mappedBy = "parentFolder")
    private List<PersonalSpaceFolder> childrenFolders;

}

