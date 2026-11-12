package org.example.placement_drive_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDto {
    private String rollNo;
    private String name;
    private String surname;
    private String email;
    private String mobileNo;
}
