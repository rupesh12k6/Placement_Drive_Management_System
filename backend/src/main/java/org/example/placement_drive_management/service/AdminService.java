package org.example.placement_drive_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.example.placement_drive_management.dto.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface AdminService {
    Page<StudentResponseDto> getAllStudents(Pageable pageable);
    StudentProfileDto getStudentProfileByRollNo(String rollNo);
    String createDrive(DriveDto driveDto);
    String createEligibility(EligibilityDto eligibilityDto);
    String updateEligibility(String driveId,EligibilityDto eligibilityDto);
    Page<StudentProfileDto> getAllProfiles(Pageable pageable);
    String publishDrivesToEligibleStudents(String driveId);
    Page<DriveDto> getAllDrives(Pageable pageable);
    Page<DriveDto> getAllDrives(String companyId, Pageable pageable);
    Page<CompanyDto> getAllCompanies(Pageable pageable);
    long countStudents();
    long countCompanies();
    long countActiveDrives();
    long countAdmins();
    List<ApplicationsDto> getAllApplicationsForaStudent(String rollNo);
    String closeDrive(String driveId);

    List<DriveRoundDto> getAllRounds(String driveId);

    List<ApplicationsDto> getAllApplications(String driveId);

    List<ApplicationRoundProjection> getApplicantsForDriveRound(String driveId, Integer roundNo);
    List<DriveDto>viewAllActiveDrives();
    String removeDrive(String driveId);
    String extendDriveApplication(String driveId, LocalDate localDate);
    String deleteDrive(String driveId);
    String deleteAdmin(Long id);
    List<AdminDto> getAllAdmins();

    ResponseEntity<StreamingResponseBody> streamStudentResume(String rollNo);
}
