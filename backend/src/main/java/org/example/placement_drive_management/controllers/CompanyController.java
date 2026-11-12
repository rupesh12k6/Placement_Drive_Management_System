package org.example.placement_drive_management.controllers;

import lombok.AllArgsConstructor;
import org.example.placement_drive_management.dto.*;
import org.example.placement_drive_management.dto.auth.ApiResponse;
import org.example.placement_drive_management.entity.Company;
import org.example.placement_drive_management.service.ApplicationRoundProjection;
import org.example.placement_drive_management.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api/company")
@AllArgsConstructor
public class CompanyController {

    private CompanyService companyService;

    @PostMapping("/publishDriveRound/{driveId}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<String>> publishDriveRound(
            @PathVariable String driveId,
            @RequestBody DriveRoundDto driveRoundDto,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Drive round published successfully", companyService.publishDriveRound(driveId, driveRoundDto, company.getCompanyId())));
    }

    @GetMapping("/getAllDrives")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<List<DriveDto>>> getAllDrives(
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Drives fetched successfully", companyService.getAllDrives(company.getCompanyId())));
    }

    @GetMapping("/getRounds/{driveId}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<List<DriveRoundDto>>> getRounds(
            @PathVariable String driveId,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Rounds fetched successfully", companyService.getAllRounds(driveId, company.getCompanyId())));
    }

    @GetMapping("/allApplications/{driveId}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<List<ApplicationsDto>>> getAllApplications(
            @PathVariable String driveId,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Applications fetched successfully", companyService.getAllApplications(driveId, company.getCompanyId())));
    }

    @GetMapping("/allApplications/{driveId}/{roundNumber}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<List<ApplicationRoundProjection>>> getAllApplicationsByRound(
            @PathVariable String driveId,
            @PathVariable Integer roundNumber,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Round applicants fetched successfully", companyService.getApplicantsForDriveRound(driveId, roundNumber, company.getCompanyId())));
    }

    @PostMapping("/publishScore/{driveId}/{rollNo}/{roundNo}/{score}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<String>> publishScore(
            @PathVariable String driveId,
            @PathVariable String rollNo,
            @PathVariable Integer roundNo,
            @PathVariable Double score,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(
                ApiResponse.success("Score published successfully", companyService.publishScoreForDriveRound(driveId, rollNo, roundNo, score, company.getCompanyId())));
    }

    @PostMapping("/publishFeedback/{driveId}/{rollNo}/{roundNo}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<String>> publishFeedback(
            @PathVariable String driveId,
            @PathVariable String rollNo,
            @PathVariable Integer roundNo,
            @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Feedback saved",
                companyService.publishFeedback(driveId, rollNo, roundNo,
                        request.getFeedback(), company.getCompanyId())));
    }

    @PostMapping("/filterByTopK/{driveId}/{roundNo}/{topK}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<String>> filterByTopKStudents(
            @PathVariable String driveId,
            @PathVariable Integer roundNo,
            @PathVariable Integer topK,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(
                ApiResponse.success("Top K filter applied successfully", companyService.filterTopKStudents(driveId, roundNo, topK, company.getCompanyId())));
    }

    @PostMapping("/filterByCutOff/{driveId}/{roundNo}/{cutOff}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<String>> filterByCutOff(
            @PathVariable String driveId,
            @PathVariable Integer roundNo,
            @PathVariable Double cutOff,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(
                ApiResponse.success("Cutoff filter applied successfully", companyService.filterByCutOffMarks(driveId, roundNo, cutOff, company.getCompanyId())));
    }

    @GetMapping("/countFilterByCutOff/{driveId}/{roundNo}/{cutOff}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<Integer>> countFilterByCutOff( @PathVariable String driveId,
                                                                    @PathVariable Integer roundNo,
                                                                    @PathVariable Double cutOff,
                                                                    @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("count by cutOff marks: ",companyService.countFilterByCutOffMarks(driveId, roundNo, cutOff, company.getCompanyId())));
    }
    @PostMapping("/shortlist/{driveId}/{roundNo}/{rollNo}/{status}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<String>> shortlistStudent(
            @PathVariable String driveId,
            @PathVariable Integer roundNo,
            @PathVariable String rollNo,
            @PathVariable String status,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Student round status updated",
                companyService.shortlistStudent(driveId, roundNo, rollNo, status, company.getCompanyId())));
    }

    @PutMapping("/closeDrive/{driveId}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<String>> closeDrive(
            @PathVariable String driveId,
            @AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Drive closed successfully",
                companyService.closeDrive(driveId, company.getCompanyId())));
    }

    @GetMapping("/Drives/count")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<Long>> countDrives(@AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Total drives count", companyService.countDrives(company.getCompanyId())));
    }

    @GetMapping("/activeDrives/count")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<ApiResponse<Long>> countActiveDrives(@AuthenticationPrincipal Company company) {
        return ResponseEntity.ok(ApiResponse.success("Active drives count", companyService.countActiveDrives(company.getCompanyId())));
    }

    /**
     * GET /api/company/viewResume/{rollNo}
     *
     * Proxies the student resume PDF inline for company iframe preview.
     * Company can only view resumes of students who applied to their drives
     * (ownership verified in service layer via driveId + rollNo).
     */
    @GetMapping("/viewResume/{rollNo}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public ResponseEntity<StreamingResponseBody> viewStudentResume(
            @PathVariable String rollNo,
            @AuthenticationPrincipal UserDetails userDetails) {
        return companyService.streamStudentResume(rollNo, userDetails.getUsername());
    }
}