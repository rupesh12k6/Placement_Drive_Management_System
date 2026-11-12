package org.example.placement_drive_management.mappers;

import org.example.placement_drive_management.dto.AdminDto;
import org.example.placement_drive_management.dto.auth.RegisterAdminRequest;
import org.example.placement_drive_management.entity.Admin;
import org.example.placement_drive_management.enums.Role;

public class AdminMapper {
    public static AdminDto mapToAdminDto(Admin admin) {
        return new AdminDto(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getRole()
        );
    }
}
