package org.example.placement_drive_management.controllers;

import org.example.placement_drive_management.dto.AdminDto;
import org.example.placement_drive_management.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
public class SuperAdminController {
    private final AdminService adminService;
    public SuperAdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @GetMapping("/allAdmins")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<AdminDto>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @DeleteMapping("/delete-admin/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteAdmin(id));
    }
}
