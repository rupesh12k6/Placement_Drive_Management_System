package org.example.placement_drive_management.service.Impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.placement_drive_management.dto.*;
import org.example.placement_drive_management.entity.*;
import org.example.placement_drive_management.exceptions.ResourceNotFoundException;
import org.example.placement_drive_management.exceptions.UnauthorizedAccessException;
import org.example.placement_drive_management.mappers.ApplicationRoundMapper;
import org.example.placement_drive_management.mappers.ApplicationsMapper;
import org.example.placement_drive_management.mappers.DriveMapper;
import org.example.placement_drive_management.mappers.DriveRoundMapper;
import org.example.placement_drive_management.repository.*;
import org.example.placement_drive_management.service.ApplicationRoundProjection;
import org.example.placement_drive_management.service.CompanyService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private DriveRoundRepository driveRoundRepository;
    private DriveRepository driveRepository;
    private ApplicationRepository applicationRepository;
    private ApplicationRoundRepository applicationRoundRepository;
    private CompanyRepository companyRepository;
    private CloudinaryService cloudinaryService;
    private StudentProfileRepository studentProfileRepository;

    private void verifyDriveOwnership(String driveId, String companyId) {
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found: " + driveId));
        if (!drive.getCompany().getCompanyId().equals(companyId)) {
            throw new UnauthorizedAccessException("You do not have permission to access this drive.");
        }
    }

    private void closeIfFinalRound(Drive drive) {
        drive.setIsActive(false);
    }

    @Override
    public String publishDriveRound(String driveId, DriveRoundDto dto, String companyId) {
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found: " + driveId));

        if (!drive.getCompany().getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You do not have permission to publish rounds for this drive.");
        }

        boolean roundExists = driveRoundRepository.existsByDrive_DriveIdAndRoundNumber(driveId, dto.getRoundNumber());
        if (roundExists) {
            return "Round " + dto.getRoundNumber() + " already exists for this drive.";
        }

        DriveRound driveRound = DriveRoundMapper.mapToDriveRound(dto);
        driveRound.setDrive(drive);
        driveRoundRepository.save(driveRound);

        String requiredStatus = (dto.getRoundNumber() == 1) ? "APPLIED" : "INPROCESS";
        List<Applications> eligibleApplications = applicationRepository.findByDrive_DriveIdAndStatus(driveId, requiredStatus);

        if (eligibleApplications.isEmpty()) {
            return "No eligible students found for round " + dto.getRoundNumber();
        }

        List<ApplicationRound> applicationRounds = new ArrayList<>();
        for (Applications application : eligibleApplications) {
            application.setCurrentRoundNumber(dto.getRoundNumber());
            ApplicationRound applicationRound = new ApplicationRound();
            applicationRound.setDriveRound(driveRound);
            applicationRound.setApplication(application);
            applicationRound.setStatus("PENDING");
            applicationRounds.add(applicationRound);
        }

        applicationRoundRepository.saveAll(applicationRounds);

        return "Round " + dto.getRoundNumber() + " published for " + eligibleApplications.size() + " students successfully.";
    }

    @Override
    public List<DriveDto> getAllDrives(String companyId) {
        List<Drive> drives = driveRepository.findByCompany_CompanyId(companyId);
        return drives.stream().map(DriveMapper::maptoDriveDto).collect(Collectors.toList());
    }

    @Override
    public List<DriveRoundDto> getAllRounds(String driveId, String companyId) {
        verifyDriveOwnership(driveId, companyId);
        List<DriveRound> driveRounds = driveRoundRepository.findByDrive_DriveId(driveId);
        return driveRounds.stream().map(DriveRoundMapper::mapToDriveRoundDto).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationsDto> getAllApplications(String driveId, String companyId) {
        verifyDriveOwnership(driveId, companyId);
        List<Applications> applications = applicationRepository.findByDrive_DriveId(driveId);
        if (applications.isEmpty()) {
            return Collections.emptyList();
        }
        return applications.stream().map(ApplicationsMapper::mapToApplicationDto).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationRoundProjection> getApplicantsForDriveRound(String driveId, Integer roundNo, String companyId) {
        verifyDriveOwnership(driveId, companyId);
        return applicationRoundRepository.findApplicantsProjected(driveId, roundNo);
    }

    @Override
    public String publishScoreForDriveRound(String driveId, String rollNo, Integer roundNo, Double score, String companyId) {
        verifyDriveOwnership(driveId, companyId);
        ApplicationRound applicationRound = applicationRoundRepository.findByDriveIdAndRollNoAndRoundNo(driveId, rollNo, roundNo)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationRound not found for Roll: " + rollNo + " Round: " + roundNo));
        applicationRound.setScore(score);
        applicationRoundRepository.save(applicationRound);
        return "Score published for RollNo: " + rollNo + " | Round: " + roundNo;
    }

    @Override
    public String publishFeedback(String driveId, String rollNo, Integer roundNo, String feedback, String companyId) {
        verifyDriveOwnership(driveId, companyId);
        ApplicationRound applicationRound = applicationRoundRepository.findByDriveIdAndRollNoAndRoundNo(driveId, rollNo, roundNo)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationRound not found"));
        applicationRound.setFeedback(feedback);
        applicationRoundRepository.save(applicationRound);
        return "Feedback saved for " + rollNo;
    }

    @Override
    public String filterTopKStudents(String driveId, Integer roundNo, Integer topK, String companyId) {
        verifyDriveOwnership(driveId, companyId);

        List<ApplicationRound> studentApplications = applicationRoundRepository.findScoredStudentsOrderByScore(driveId, roundNo);

        if (studentApplications.isEmpty()) {
            throw new ResourceNotFoundException("No scored students found for this round.");
        }
        if (topK <= 0 || topK > studentApplications.size()) {
            throw new IllegalArgumentException("Invalid topK. Must be between 1 and " + studentApplications.size());
        }

        Drive drive = driveRepository.findByDriveId(driveId).orElseThrow(() -> new ResourceNotFoundException("Drive not found"));
        if (drive.getMaxRounds().equals(roundNo)) {
            closeIfFinalRound(drive);
        }

        String appRoundstatus = drive.getMaxRounds().equals(roundNo) ? "SELECTED" : "CLEARED";
        String appStatus = drive.getMaxRounds().equals(roundNo) ? "SELECTED" : "INPROCESS";

        List<ApplicationRound> roundsToUpdate = new ArrayList<>();
        List<Applications> applicationsToUpdate = new ArrayList<>();

        for (int i = 0; i < studentApplications.size(); i++) {
            ApplicationRound appRound = studentApplications.get(i);
            Applications application = appRound.getApplication();

            if (i < topK) {
                appRound.setStatus(appRoundstatus);
                application.setStatus(appStatus);
            } else {
                appRound.setStatus("FAILED");
                application.setStatus("REJECTED");
            }
            application.setCurrentRoundNumber(roundNo);
            roundsToUpdate.add(appRound);
            applicationsToUpdate.add(application);
        }

        applicationRoundRepository.saveAll(roundsToUpdate);
        applicationRepository.saveAll(applicationsToUpdate);

        return "Top " + topK + " students cleared out of " + studentApplications.size() + " scored students.";
    }

    @Override
    public String filterByCutOffMarks(String driveId, Integer roundNo, Double cutOff, String companyId) {
        verifyDriveOwnership(driveId, companyId);

        List<ApplicationRound> studentApplications = applicationRoundRepository.findScoredStudentsOrderByScore(driveId, roundNo);

        if (studentApplications.isEmpty()) {
            throw new ResourceNotFoundException("No scored students found for this round.");
        }

        Drive drive = driveRepository.findByDriveId(driveId).orElseThrow(() -> new ResourceNotFoundException("Drive not found"));
        if (drive.getMaxRounds().equals(roundNo)) {
            closeIfFinalRound(drive);
        }

        String appRoundstatus = drive.getMaxRounds().equals(roundNo) ? "SELECTED" : "CLEARED";
        String appStatus = drive.getMaxRounds().equals(roundNo) ? "SELECTED" : "INPROCESS";

        List<ApplicationRound> roundsToUpdate = new ArrayList<>();
        List<Applications> applicationsToUpdate = new ArrayList<>();
        int cleared = 0, failed = 0;

        for (ApplicationRound appRound : studentApplications) {
            Applications application = appRound.getApplication();

            if (appRound.getScore() >= cutOff) {
                appRound.setStatus(appRoundstatus);
                application.setStatus(appStatus);
                cleared++;
            } else {
                appRound.setStatus("FAILED");
                application.setStatus("REJECTED");
                failed++;
            }
            application.setCurrentRoundNumber(roundNo);
            roundsToUpdate.add(appRound);
            applicationsToUpdate.add(application);
        }

        applicationRoundRepository.saveAll(roundsToUpdate);
        applicationRepository.saveAll(applicationsToUpdate);

        return "Cutoff: " + cutOff + " | Cleared: " + cleared + " | Failed: " + failed;
    }

    @Override
    public Integer countFilterByCutOffMarks(String driveId, Integer roundNo, Double cutOffMarks, String companyId) {
        verifyDriveOwnership(driveId, companyId);
        return (int) applicationRoundRepository.countScoredStudentsAboveCutOff(driveId, roundNo, cutOffMarks);
    }

    @Override
    public String shortlistStudent(String driveId, Integer roundNo, String rollNo, String status, String companyId) {
        verifyDriveOwnership(driveId, companyId);
        String normalized = status == null ? "" : status.trim().toUpperCase();
        if (!normalized.equals("CLEARED") && !normalized.equals("FAILED")) {
            throw new IllegalArgumentException("Status must be CLEARED or FAILED");
        }

        ApplicationRound appRound = applicationRoundRepository.findByDriveIdAndRollNoAndRoundNo(driveId, rollNo, roundNo)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationRound not found for Roll: " + rollNo + " Round: " + roundNo));
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found: " + driveId));
        Applications application = appRound.getApplication();

        boolean isFinalRound = drive.getMaxRounds() != null && drive.getMaxRounds().equals(roundNo);
        if (normalized.equals("CLEARED")) {
            appRound.setStatus(isFinalRound ? "SELECTED" : "CLEARED");
            application.setStatus(isFinalRound ? "SELECTED" : "INPROCESS");
            if (isFinalRound) {
                closeIfFinalRound(drive);
            }
        } else {
            appRound.setStatus("FAILED");
            application.setStatus("REJECTED");
        }
        application.setCurrentRoundNumber(roundNo);
        applicationRoundRepository.save(appRound);
        applicationRepository.save(application);
        return rollNo + " marked " + normalized;
    }

    @Override
    public String closeDrive(String driveId, String companyId) {
        verifyDriveOwnership(driveId, companyId);
        Drive drive = driveRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResourceNotFoundException("Drive not found: " + driveId));
        drive.setIsActive(false);
        driveRepository.save(drive);
        return "Drive closed successfully";
    }

    @Override
    public long countDrives(String companyId) {
        return driveRepository.countByCompany_CompanyId(companyId);
    }

    @Override
    public long countActiveDrives(String companyId) {
        return driveRepository.countByCompany_CompanyIdAndIsActive(companyId, true);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> streamStudentResume(String rollNo, String companyEmail) {
        Company company = companyRepository.findByEmail(companyEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyEmail));

        boolean hasApplication = applicationRepository.existsByStudent_RollNoAndDrive_Company_CompanyId(rollNo, company.getCompanyId());
        if (!hasApplication) {
            throw new UnauthorizedAccessException("No application found for this student in your drives.");
        }

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
