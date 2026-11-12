package org.example.placement_drive_management.dto;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDto {
    Long id;
    String rollNo;
    String name;
    String surname;
    String email;
    String mobileNo;
    String password;
}
