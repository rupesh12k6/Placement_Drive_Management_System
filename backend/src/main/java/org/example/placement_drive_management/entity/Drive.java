package org.example.placement_drive_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Drive {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String driveId;
    private String driveName;
    private String jobRole;
    private Double packageOffered;

    private String jobLocation;

    private Integer vacancies;

    private LocalDate registrationStartDate;

    private LocalDate registrationEndDate;
    private Integer maxRounds;
    private Boolean isActive=false;
    private String externalLink;

    @ManyToOne
    @JoinColumn(name = "company_id",referencedColumnName = "companyId")
    private Company company;
    private String description;
    @OneToOne(mappedBy = "drive", cascade = CascadeType.ALL,orphanRemoval = true)
    private Eligibility eligibility;

    @OneToMany(mappedBy = "drive", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Applications> applications;

    @OneToMany(mappedBy = "drive", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<DriveRound> rounds;


}
