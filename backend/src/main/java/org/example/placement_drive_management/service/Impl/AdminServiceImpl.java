package org.example.placement_drive_management.service.Impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.placement_drive_management.dto.*;
import org.example.placement_drive_management.entity.*;
import org.example.placement_drive_management.enums.Role;
import org.example.placement_drive_management.exceptions.ResourceNotFoundException;
import org.example.placement_drive_management.mappers.*;
import org.example.placement_drive_management.repository.*;
import org.example.placement_drive_management.service.AdminService;
import org.example.placement_drive_management.service.ApplicationRoundProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {
    private AdminRepository adminRepository;
    private StudentRepository studentRepository;
    private StudentProfileRepository studentProfileRepository;
    private CompanyRepository companyRepository;
    private DriveRepository driveRepository;
    private EligibilityRepository eligibilityRepository;
    private DriveRoundRepository driveRoundRepository;
    private ApplicationRoundRepository applicationRoundRepository;
    private ApplicationRepository applicationRepository;
    private CloudinaryService cloudinaryService;

    public void updateEligibilityFields(Eligibility eligibility, EligibilityDto dto) {
        eligibility.setMinimumCgpa(dto.getMinimumCgpa());
        eligibility.setMaxActiveBacklogs(dto.getMaxActiveBacklogs());
        eligibility.setAllowedBranch(dto.getAllowedBranch());
        eligibility.setPassingYear(dto.getPassingYear());
        eligibility.setGender(dto.getGender());
        eligibility.setHasHistoryBacklogs(dto.getHasHistoryBacklogs());
    }

    @Override
    public Page<StudentResponseDto> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).map(StudentMapper::maptoStudentResponseDto);
    }

    @Override
    public Page<StudentProfileDto> getAllProfiles(Pageable pageable) {
        return studentProfileRepository.findAll(pageable).map(StudentProfileMapper::maptoStudentProfileDto);
    }

    @Override
    public StudentProfileDto getStudentProfileByRollNo(String rollNo) {
        StudentProfile studentProfile = studentProfileRepository.findByStudentRollNo(rollNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student with RollNo " + rollNo + " profile not found"));
        return StudentProfileMapper.maptoStudentProfileDto(studentProfile);
    }

    @Override
    public String createDrive(DriveDto driveDto) {
        if (driveRepository.existsByDriveId(driveDto.getDriveId())) {
            return "drive already exists";
        }
        Drive newdrive = DriveMapper.maptoDrive(driveDto);
        Company company = companyRepository.findByCompanyId(driveDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company with id " + driveDto.getCompanyId() + " not found"));
        newdrive.setCompany(company);
        newdrive.setMaxRounds(driveDto.getMaxRounds());
        driveRepository.save(newdrive);
        return "drive created ";
    }

    @Override
    public String createEligibility(EligibilityDto eligibilityDto) {
        Drive drive = driveRepository.findByDriveId(eligibilityDto.getDriveId())
                .orElseThrow(() -> new ResourceNotFoundException("drive not found"));
        Eligibility eligibility = new Eligibility(
                eligibilityDto.getId(),
                eligibilityDto.getMinimumCgpa(),
                eligibilityDto.getMaxActiveBacklogs(),
                eligibilityDto.getAllowedBranch(),
                eligibilityDto.getPassingYear(),
                eligibilityDto.getGender(),
                eligibilityDto.getHasHistoryBacklogs(),
                drive
        );
        drive.setEligibility(eligibility);
        eligibilityRepository.save(eligibility);
        return "Eligiblity created successfully";
    }

    @Override
    public String updateEligibility(String driveId, EligibilityDto eligibilityDto) {
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found"));

        if (!drive.getRounds().isEmpty()) {
            return "Eligibility cannot be updated once rounds are added";
        }

        Eligibility eligibility = eligibilityRepository.findByDrive_DriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Eligibility not found"));

        updateEligibilityFields(eligibility, eligibilityDto);
        eligibilityRepository.save(eligibility);

        if (Boolean.TRUE.equals(drive.getIsActive())) {
            applicationRepository.deleteByDrive_DriveId(driveId);
            drive.getApplications().clear();
            drive.setIsActive(false);
            driveRepository.save(drive);
            return publishDrivesToEligibleStudents(driveId);
        }

        return "Successfully updated Eligibility";
    }

    @Override
    public String publishDrivesToEligibleStudents(String driveId) {
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("drive not found"));
        
        if (Boolean.TRUE.equals(drive.getIsActive())) {
            return "Eligible student has been published";
        }
        
        drive.setIsActive(true);
        Eligibility eligibility = eligibilityRepository.findByDrive_DriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive Eligibility Not Found"));

        // Issue 1: Optimized filtering (DB-side)
        List<String> branches = Arrays.stream(eligibility.getAllowedBranch().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        List<StudentProfile> profiles = studentProfileRepository.findEligibleStudents(
                branches,
                eligibility.getPassingYear(),
                eligibility.getMinimumCgpa(),
                eligibility.getMaxActiveBacklogs(),
                eligibility.getGender(),
                eligibility.getHasHistoryBacklogs()
        );

        // Issue 3: Bulk insert
        List<Applications> applicationsList = new ArrayList<>();
        for (StudentProfile profile : profiles) {
            Applications application = new Applications();
            application.setStudent(profile.getStudent());
            application.setStudentProfile(profile);
            application.setDrive(drive);
            application.setStatus("ELIGIBLE");
            
            // Link to drive and profile for consistency
            drive.getApplications().add(application);
            profile.getApplicationsList().add(application);
            
            applicationsList.add(application);
        }
        
        applicationRepository.saveAll(applicationsList);
        driveRepository.save(drive); // Update drive state (isActive)

        return "Drives published successfully for " + applicationsList.size() + " students";
    }

    @Override
    public Page<DriveDto> getAllDrives(Pageable pageable) {
        return driveRepository.findAll(pageable).map(DriveMapper::maptoDriveDto);
    }

    @Override
    public Page<DriveDto> getAllDrives(String companyId, Pageable pageable) {
        // Issue 7: Optimized getAllDrives - fetch directly by companyId
        return driveRepository.findByCompany_CompanyId(companyId, pageable).map(DriveMapper::maptoDriveDto);
    }

    @Override
    public Page<CompanyDto> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable).map(CompanyMapper::mapCompanyToDto);
    }

    @Override
    public long countStudents() {
        return studentRepository.count();
    }

    @Override
    public long countCompanies() {
        return companyRepository.count();
    }

    @Override
    public long countActiveDrives() {
        return driveRepository.countByIsActive(true);
    }

    @Override
    public long countAdmins() {
        return adminRepository.countByRoleNot(Role.ROLE_SUPER_ADMIN);
    }

    @Override
    public List<ApplicationsDto> getAllApplicationsForaStudent(String rollNo) {
        List<Applications> applications = applicationRepository.findByStudent_RollNo(rollNo);
        return applications.stream().map(ApplicationsMapper::mapToApplicationDto).collect(Collectors.toList());
    }

    @Override
    public String closeDrive(String driveId) {
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found"));
        drive.setIsActive(false);
        driveRepository.save(drive);
        return "Drive closed successfully";
    }

    @Override
    public List<DriveRoundDto> getAllRounds(String driveId) {
        List<DriveRound> driveRounds = driveRoundRepository.findByDrive_DriveId(driveId);
        return driveRounds.stream().map(DriveRoundMapper::mapToDriveRoundDto).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationsDto> getAllApplications(String driveId) {
        List<Applications> applications = applicationRepository.findByDrive_DriveId(driveId);
        if (applications.isEmpty()) {
            return Collections.emptyList();
        }
        return applications.stream().map(ApplicationsMapper::mapToApplicationDto).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationRoundProjection> getApplicantsForDriveRound(String driveId, Integer roundNo) {
        return applicationRoundRepository.findApplicantsProjected(driveId, roundNo);
    }

    @Override
    public List<DriveDto> viewAllActiveDrives() {
        return driveRepository.findAllByIsActive(true).stream()
                .map(DriveMapper::maptoDriveDto)
                .collect(Collectors.toList());
    }

    @Override
    public String removeDrive(String driveId) {
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("drive not found"));
        if (Boolean.TRUE.equals(drive.getIsActive())) {
            return "Drive is Already Published ,You cannot remove Now";
        }
        driveRepository.delete(drive);
        return "Drive has been removed successfully";
    }

    @Override
    public String extendDriveApplication(String driveId, LocalDate localDate) {
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("drive not found"));
        if (drive.getRounds().isEmpty() && localDate.isAfter(drive.getRegistrationEndDate())) {
            drive.setRegistrationEndDate(localDate);
            return "Registration date has been extended to " + localDate;
        }
        return "You cannot update the Registration Date now";
    }

    @Override
    public String deleteDrive(String driveId) {
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive with driveId " + driveId + " is not found"));

        if (Boolean.TRUE.equals(drive.getIsActive()) && !drive.getRounds().isEmpty()) {
            return "Cannot delete drive because rounds already exist";
        }

        driveRepository.delete(drive);
        return "Successfully Deleted Drive with ID " + driveId;
    }

    @Override
    public String deleteAdmin(Long id) {
        adminRepository.deleteById(id);
        return "Successfully Deleted Admin";
    }

    @Override
    public List<AdminDto> getAllAdmins() {
        return adminRepository.findAll().stream()
                .filter(admin -> admin.getRole() != Role.ROLE_SUPER_ADMIN)
                .map(AdminMapper::mapToAdminDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<StreamingResponseBody> streamStudentResume(String rollNo) {
        StudentProfile profile = studentProfileRepository.findByStudentRollNo(rollNo)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for rollNo: " + rollNo));
        
        if (profile.getResumeUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody responseBody = outputStream -> {
            try (InputStream inputStream = cloudinaryService.fetchResumeStream(profile.getResumeUrl())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + rollNo + "_resume.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(responseBody);
    }
}
