package org.example.placement_drive_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriveRoundDto {
    private Long id;
    private Integer roundNumber;
    private String roundName;
    private LocalDate roundDate;
    private String roundLink;
}
