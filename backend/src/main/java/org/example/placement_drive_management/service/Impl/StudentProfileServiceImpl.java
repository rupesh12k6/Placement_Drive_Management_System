package org.example.placement_drive_management.service.Impl;

import jakarta.transaction.Transactional;
import org.example.placement_drive_management.dto.*;
import org.example.placement_drive_management.entity.*;
import org.example.placement_drive_management.exceptions.ResourceNotFoundException;
import org.example.placement_drive_management.exceptions.UnauthorizedAccessException;
import org.example.placement_drive_management.mappers.ApplicationRoundMapper;
import org.example.placement_drive_management.mappers.ApplicationsMapper;
import org.example.placement_drive_management.mappers.DriveRoundMapper;
import org.example.placement_drive_management.mappers.StudentProfileMapper;
import org.example.placement_drive_management.repository.*;
import org.example.placement_drive_management.service.StudentProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;
    private final DriveRepository driveRepository;
    private final ApplicationRoundRepository applicationRoundRepository;
    private final CloudinaryService cloudinaryService;

    public StudentProfileServiceImpl(StudentProfileRepository studentProfileRepository,
            StudentRepository studentRepository, ApplicationRepository applicationRepository,
            DriveRepository driveRepository, ApplicationRoundRepository applicationRoundRepository,
            CloudinaryService cloudinaryService) {
        this.studentProfileRepository = studentProfileRepository;
        this.studentRepository = studentRepository;
        this.applicationRepository = applicationRepository;
        this.driveRepository = driveRepository;
        this.applicationRoundRepository = applicationRoundRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public String createStudentProfile(StudentProfileDto studentProfileDto, String rollNo) {
        Student student = studentRepository.findByRollNo(rollNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student with RollNo "
                        + rollNo + " not found"));

        if (studentProfileRepository.existsByStudent(student)) {
            throw new IllegalArgumentException("Student profile already exists for rollNo: " + rollNo);
        }

        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setStudent((student));
        studentProfile.setDepartment(studentProfileDto.getDepartment());
        studentProfile.setCurrentSemester(studentProfileDto.getCurrentSemester());
        studentProfile.setHasbackloghistory(studentProfileDto.getHasBacklogHistory());
        studentProfile.setBacklogCount(studentProfileDto.getBacklogCount());
        studentProfile.setCurrentCgpa(studentProfileDto.getCurrentCgpa());
        studentProfile.setTenthPercentage(studentProfileDto.getTenthPercentage());
        studentProfile.setDiplomaPercentage(studentProfileDto.getDiplomaPercentage());
        studentProfile.setTwelthPercentage(studentProfileDto.getTwelthPercentage());
        studentProfile.setGender(studentProfileDto.getGender());
        studentProfile.setPassingYear(studentProfileDto.getPassingYear());
        studentProfileRepository.save(studentProfile);

        return "Profile Created Successfully";
    }

    @Override
    public String updateStudentProfile(
            StudentProfileDto studentProfileDto, String rollNo) {

        StudentProfile existingProfile = studentProfileRepository.findByStudentRollNo(rollNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student with RollNo "
                        + rollNo + " not found"));

        existingProfile.setDepartment(studentProfileDto.getDepartment());
        existingProfile.setTenthPercentage(studentProfileDto.getTenthPercentage());
        existingProfile.setTwelthPercentage(studentProfileDto.getTwelthPercentage());
        existingProfile.setDiplomaPercentage(studentProfileDto.getDiplomaPercentage());
        existingProfile.setCurrentCgpa(studentProfileDto.getCurrentCgpa());
        existingProfile.setCurrentSemester(studentProfileDto.getCurrentSemester());
        existingProfile.setBacklogCount(studentProfileDto.getBacklogCount());
        existingProfile.setHasbackloghistory(studentProfileDto.getHasBacklogHistory());
        existingProfile.setGender(studentProfileDto.getGender());
        existingProfile.setPassingYear(studentProfileDto.getPassingYear());
        studentProfileRepository.save(existingProfile);

        return "Profile Updated Successfully";
    }

    @Override
    public StudentProfileDto getStudentProfile(String rollNo) {
        StudentProfile studentProfile = studentProfileRepository.findByStudentRollNo(rollNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student with RollNo "
                        + rollNo + " not found"));
        return StudentProfileMapper.maptoStudentProfileDto(studentProfile);
    }

    private DriveInfoDto buildDriveInfo(Drive drive) {
        DriveInfoDto driveInfoDto = new DriveInfoDto();
        driveInfoDto.setCompanyName(drive.getCompany().getCompanyName());
        driveInfoDto.setDriveName(drive.getDriveName());
        driveInfoDto.setRole(drive.getJobRole());
        driveInfoDto.setPackageAmount(drive.getPackageOffered());
        driveInfoDto.setJobLocation(drive.getJobLocation());
        driveInfoDto.setRegistrationEndDate(drive.getRegistrationEndDate());
        return driveInfoDto;
    }

    @Override
    public List<ApplicationsDto> getAllApplicationsForStudent(String studentRollNo) {
        StudentProfile studentProfile = studentProfileRepository.findByStudentRollNo(studentRollNo).orElseThrow(
                () -> new ResourceNotFoundException("Student with Roll No :" + studentRollNo + "not found"));
        List<Applications> getApplications = studentProfile.getApplicationsList();
        List<ApplicationsDto> applicationsDtos = new ArrayList<>();
        for (Applications application : getApplications) {
            if (application.getStatus().equals("ELIGIBLE")) {
                continue;
            }
            ApplicationsDto applicationsDto = ApplicationsMapper.mapToApplicationDto(application);
            applicationsDto.setDriveInfo(buildDriveInfo(application.getDrive()));
            applicationsDtos.add(applicationsDto);
        }
        return applicationsDtos;
    }

    @Override
    public List<ApplicationRoundDto> getAllApplicationRoundsForStudentAndDriveId(String driveId,
            String rollNumberInContext) {
        List<ApplicationRound> applicationRounds = applicationRoundRepository.findAllRoundDetails(driveId,
                rollNumberInContext);
        return applicationRounds.stream().map(ApplicationRoundMapper::maptoApplicationRoundDto)
                .collect(Collectors.toList());
    }

    @Override
    public String applyDrive(String driveId, String rollNo) {
        Applications application = applicationRepository.findByDrive_DriveIdAndStudent_RollNo(driveId, rollNo)
                .orElseThrow(() -> new ResourceNotFoundException("application not found"));
        if (application.getStatus().equals("APPLIED")) {
            return "You have already applied this application";
        }
        if (!application.getStatus().equals("ELIGIBLE")) {
            throw new IllegalArgumentException("Application cannot be submitted in status: " + application.getStatus());
        }
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive with DriveId not found"));
        if (!Boolean.TRUE.equals(drive.getIsActive())) {
            throw new IllegalArgumentException("This placement drive is closed");
        }
        LocalDate start = drive.getRegistrationStartDate();
        LocalDate end = drive.getRegistrationEndDate();
        if (LocalDate.now().isBefore(start) || LocalDate.now().isAfter(end)) {
            return "Application time is over";
        }
        application.setAppliedDate(LocalDate.now());
        application.setCurrentRoundNumber(0);
        application.setStatus("APPLIED");
        application.setExternalApplied(false);
        applicationRepository.save(application);
        return "application applied for " + driveId + " successfully";
    }

    @Override
    public List<ApplicationsDto> getAllEligibleApplications(String rollNo) {
        StudentProfile studentProfile = studentProfileRepository.findByStudentRollNo(rollNo)
                .orElseThrow(() -> new ResourceNotFoundException("Student with Roll No :" + rollNo + "not found"));
        List<Applications> getApplications = studentProfile.getApplicationsList();
        List<ApplicationsDto> applicationsDtos = new ArrayList<>();
        for (Applications application : getApplications) {
            if (!application.getStatus().equals("ELIGIBLE")) {
                continue;
            }
            ApplicationsDto applicationsDto = ApplicationsMapper.mapToApplicationDto(application);
            applicationsDto.setDriveInfo(buildDriveInfo(application.getDrive()));
            applicationsDtos.add(applicationsDto);
        }
        return applicationsDtos;
    }

    private Student getStudentByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student not found with email: " + email));
    }

    private StudentProfile getProfileByEmail(String email) {
        Student student = getStudentByEmail(email);
        return studentProfileRepository.findByStudentRollNo(student.getRollNo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found for student: " + email));
    }

    @Override
    public String uploadResume(MultipartFile file, String email) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("File must not be empty");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf"))
            throw new IllegalArgumentException("Only PDF files are accepted");

        if (file.getSize() > 5 * 1024 * 1024)
            throw new IllegalArgumentException("File size must be under 5 MB");

        StudentProfile profile = getProfileByEmail(email);
        Student student = profile.getStudent();

        try {
            // resource_type=raw → PDF stored verbatim, never garbled on download
            // public_id=rollNo → re-uploads overwrite same file, no duplicates
            String secureUrl = cloudinaryService.uploadResume(file, student.getRollNo());
            profile.setResumeUrl(secureUrl);
            studentProfileRepository.save(profile);
            return "Resume uploaded successfully";
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to upload resume: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<byte[]> streamResume(String email) {
        StudentProfile profile = getProfileByEmail(email);
        if (profile.getResumeUrl() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] bytes = cloudinaryService.fetchResumeBytes(profile.getResumeUrl());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // inline = render in browser, not download
            headers.setContentDispositionFormData("inline", "resume.pdf");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"resume.pdf\"");
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch resume: " + e.getMessage(), e);
        }
    }
}
