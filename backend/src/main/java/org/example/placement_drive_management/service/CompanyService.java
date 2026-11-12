package org.example.placement_drive_management.service;
import org.example.placement_drive_management.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

public interface CompanyService {

    String publishDriveRound(String driveId, DriveRoundDto driveRoundDto, String companyId);

    List<DriveDto> getAllDrives(String companyId);

    List<DriveRoundDto> getAllRounds(String driveId, String companyId);

    List<ApplicationsDto> getAllApplications(String driveId, String companyId);

    List<ApplicationRoundProjection> getApplicantsForDriveRound(String driveId, Integer roundNo, String companyId);

    String publishScoreForDriveRound(String driveId, String rollNo, Integer roundNo, Double score, String companyId);
    String publishFeedback(String driveId, String rollNo, Integer roundNo, String feedback, String companyId);
    String filterTopKStudents(String driveId, Integer roundNo, Integer topK, String companyId);

    String filterByCutOffMarks(String driveId, Integer roundNo, Double cutOffMarks, String companyId);
    Integer countFilterByCutOffMarks(String driveId, Integer roundNo,Double cutOffMarks, String companyId);

    String shortlistStudent(String driveId, Integer roundNo, String rollNo, String status, String companyId);
    String closeDrive(String driveId, String companyId);
    long countDrives(String companyId);
    long countActiveDrives(String companyId);

    ResponseEntity<StreamingResponseBody> streamStudentResume(String rollNo, String companyEmail);
}