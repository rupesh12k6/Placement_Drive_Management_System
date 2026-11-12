package org.example.placement_drive_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Eligibility {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Double minimumCgpa;

    private Integer maxActiveBacklogs;

    private String allowedBranch;

    private Integer passingYear;
    @Pattern(regexp = "^(FEMALE|MALE|BOTH)$",message = "not eligible")
    private String gender;
    private Boolean hasHistoryBacklogs;
    @OneToOne
    @JoinColumn(name = "drive_id",referencedColumnName = "driveId",nullable = false)
    private Drive drive;
}