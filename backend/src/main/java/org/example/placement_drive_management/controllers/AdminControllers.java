package org.example.placement_drive_management.controllers;

import org.example.placement_drive_management.dto.*;
import org.example.placement_drive_management.dto.auth.ApiResponse;
import org.example.placement_drive_management.service.AdminService;
import org.example.placement_drive_management.service.ApplicationRoundProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminControllers {

    private final AdminService adminService;

    public AdminControllers(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/allStudents")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Page<StudentResponseDto>> getAllStudents(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllStudents(pageable));
    }

    @GetMapping("/allStudentProfiles")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Page<StudentProfileDto>> getAllStudentProfiles(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllProfiles(pageable));
    }

    @GetMapping("/student/{rollNo}/profile")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<StudentProfileDto> getStudentProfile(@PathVariable("rollNo") String rollNo) {
        return ResponseEntity.ok(adminService.getStudentProfileByRollNo(rollNo));
    }

    @PostMapping("/company/addDrive")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> addDrive(@RequestBody DriveDto driveDto) {
        return ResponseEntity.ok(adminService.createDrive(driveDto));
    }

    @PostMapping("/company/addDriveEligibility")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> createEligibility(@RequestBody EligibilityDto eligibilityDto) {
        return ResponseEntity.ok(adminService.createEligibility(eligibilityDto));
    }

    @PutMapping("/publishDrives/{driveId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> publishDrives(@PathVariable("driveId") String driveId) {
        return ResponseEntity.ok(adminService.publishDrivesToEligibleStudents(driveId));
    }

    @GetMapping("/allDrives")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Page<DriveDto>> getAllDrives(@PageableDefault(size = 15) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllDrives(pageable));
    }

    @GetMapping("/company/{companyId}/drives")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Page<DriveDto>> getAllDrives(@PathVariable("companyId") String companyId, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllDrives(companyId, pageable));
    }

    @GetMapping("/allCompanies")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Page<CompanyDto>> getAllCompanies(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllCompanies(pageable));
    }

    @GetMapping("/students/count")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Long> countStudents() {
        return ResponseEntity.ok(adminService.countStudents());
    }

    @GetMapping("/companies/count")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Long> countCompanies() {
        return ResponseEntity.ok(adminService.countCompanies());
    }

    @GetMapping("/activeDrives/count")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<Long> countActiveDrives() {
        return ResponseEntity.ok(adminService.countActiveDrives());
    }

    @GetMapping("/admins/count")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Long> countAdmins() {
        return ResponseEntity.ok(adminService.countAdmins());
    }

    @GetMapping("student/allApplications/{rollNo}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ApplicationsDto>> getAllApplicationsForStudent(@PathVariable String rollNo) {
        return ResponseEntity.ok(adminService.getAllApplicationsForaStudent(rollNo));
    }

    @PutMapping("/closeDrive/{drivveId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> closeDrive(@PathVariable("drivveId") String drivveId) {
        return ResponseEntity.ok(adminService.closeDrive(drivveId));
    }

    @GetMapping("/getAllActiveDrives")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<DriveDto>> getAllActiveDrives() {
        return ResponseEntity.ok(adminService.viewAllActiveDrives());
    }

    @PutMapping("/removeDrive/{driveId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> removeDrive(@PathVariable("driveId") String driveId) {
        return ResponseEntity.ok(adminService.removeDrive(driveId));
    }

    @PutMapping("/extendDrive/{driveId}/{localDate}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> extendDate(@PathVariable("driveId") String driveId, @PathVariable("localDate") LocalDate localDate) {
        return ResponseEntity.ok(adminService.extendDriveApplication(driveId, localDate));
    }

    @PutMapping("/updateDriveEligibility/{driveId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> updateEligibility(@PathVariable("driveId") String driveId,@RequestBody EligibilityDto eligibilityDto) {
        return ResponseEntity.ok(adminService.updateEligibility(driveId,eligibilityDto));
    }

    @GetMapping("/getAllDriveRounds/{driveId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<DriveRoundDto>> getAllRounds(@PathVariable String driveId){
        return ResponseEntity.ok(adminService.getAllRounds(driveId));
    }

    @GetMapping("/getAllApplications/{driveId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ApplicationsDto>> getAllApplications(@PathVariable String driveId){
        return ResponseEntity.ok(adminService.getAllApplications(driveId));
    }

    @GetMapping("/getApplicantsForDriveRound/{driveId}/{roundNo}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ApplicationRoundProjection>> getApplicantsForDriveRound(@PathVariable String driveId, @PathVariable Integer roundNo) {
        return ResponseEntity.ok(adminService.getApplicantsForDriveRound(driveId,roundNo));
    }

    @DeleteMapping("/deleteDrive/{driveId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> deleteDrive(@PathVariable("driveId") String driveId) {
        return ResponseEntity.ok(adminService.deleteDrive(driveId));
    }

    @GetMapping("/student/{rollNo}/viewResume")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    public ResponseEntity<StreamingResponseBody> viewStudentResume(@PathVariable String rollNo) {
        return adminService.streamStudentResume(rollNo);
    }
}
