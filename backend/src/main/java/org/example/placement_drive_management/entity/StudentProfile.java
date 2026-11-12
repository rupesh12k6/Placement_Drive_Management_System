package org.example.placement_drive_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "student_profile")
@Table(name = "student_profile", indexes = {
        @Index(name = "idx_student_profile_roll", columnList = "student_roll_number")
})
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_profile_seq")
    @SequenceGenerator(name = "student_profile_seq", allocationSize = 50)
    private Long id;

    @OneToOne
    @JoinColumn(name = "student_roll_number", referencedColumnName = "rollNo", unique = true)
    private Student student;

    @NonNull
    @Pattern(regexp = "^(CSE|ECE|EEE|CHEM|IT|CSM|CSD|MECH|CIVIL)$")
    private String department;

    @DecimalMin(value = "44.0", message = "Enter Valid 10th marks percentage")
    @DecimalMax(value = "100.0", message = "Enter Valid 10th marks percentage")
    private Double tenthPercentage;

    @DecimalMin(value = "44.0", message = "Enter Valid 12th marks percentage")
    @DecimalMax(value = "100.0", message = "Enter Valid 12th marks percentage")
    private Double twelthPercentage;

    @DecimalMin(value = "44.0", message = "Enter Valid Diploma percentage")
    @DecimalMax(value = "100.0", message = "Enter Valid Diploma percentage")
    private Double diplomaPercentage;

    @DecimalMin(value = "4.0", message = "Enter Valid CGPA")
    @DecimalMax(value = "10.0", message = "Enter Valid CGPA")
    private Double currentCgpa;

    @Min(value = 1, message = "Enter Valid Semester")
    @Max(value = 8, message = "Enter Valid Semester")
    private Integer currentSemester;

    @Min(value = 0, message = "Enter Valid Backlog Count")
    @Max(value = 55, message = "Enter Valid Backlog Count")
    private Integer backlogCount;

    @Column(nullable = false)
    private Boolean hasbackloghistory;

    private Integer passingYear;

    @Pattern(regexp = "^(MALE|FEMALE|OTHERS)$", message = "Gender should be MALE|FEMALE|OTHERS")
    private String gender;

    @Column(length = 1024)
    private String resumeUrl;

    @OneToMany(mappedBy = "studentProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Applications> applicationsList;
}
