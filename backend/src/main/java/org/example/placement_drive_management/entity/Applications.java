package org.example.placement_drive_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "applications", indexes = {
        @Index(name = "idx_applications_drive_status", columnList = "drive_id, status"),
        @Index(name = "idx_applications_student", columnList = "student_id")
})
public class Applications {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "applications_seq")
    @SequenceGenerator(name = "applications_seq", allocationSize = 50)
    private Long id;

    @Pattern(
            regexp = "^(ELIGIBLE|SHORTLISTED|APPLIED|REJECTED|INPROCESS|SELECTED)$",
            message = "Application status must be SHORTLISTED, APPLIED, REJECTED, INPROCESS, or SELECTED"
    )
    private String status;

    private Integer currentRoundNumber;

    private Boolean externalApplied;

    private LocalDate appliedDate;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "rollNo")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "drive_id", referencedColumnName = "driveId")
    private Drive drive;

    @ManyToOne
    @JoinColumn(name = "student_profile")
    private StudentProfile studentProfile;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationRound> applicationRounds;
}
