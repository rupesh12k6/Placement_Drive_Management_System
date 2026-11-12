package org.example.placement_drive_management.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.placement_drive_management.enums.Role;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminDto {
    private Long id;
    private String name;
    private String email;
    private Role role= Role.ROLE_ADMIN;
}
