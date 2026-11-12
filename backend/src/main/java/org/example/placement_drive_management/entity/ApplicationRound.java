package org.example.placement_drive_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "applicationRound")
@Table(name = "application_round", indexes = {
        @Index(name = "idx_app_round_drive_round", columnList = "drive_round_id")
})
public class ApplicationRound {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_round_seq")
    @SequenceGenerator(name = "app_round_seq", allocationSize = 50)
    private Long id;

    private String status;

    private Double score;

    private String feedback;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Applications application;

    @ManyToOne
    @JoinColumn(name = "drive_round_id")
    private DriveRound driveRound;
}
