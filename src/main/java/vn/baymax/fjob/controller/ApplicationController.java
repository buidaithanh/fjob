package vn.baymax.fjob.controller;

import vn.baymax.fjob.domain.Application;
import vn.baymax.fjob.domain.User;
import vn.baymax.fjob.domain.Application.ApplicationStatus;
import vn.baymax.fjob.dto.request.ApplicationDTO;
import vn.baymax.fjob.dto.request.ApplicationRequestDTO;
import vn.baymax.fjob.dto.request.ApplicationStatsDTO;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.service.ApplicationService;
import vn.baymax.fjob.service.UserService;
import vn.baymax.fjob.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

        private final ApplicationService applicationService;
        private final UserService userService;

        @Operation(summary = "Create application", description = "Apply for a job")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Application created successfully", content = @Content(schema = @Schema(implementation = ApplicationDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        @PostMapping
        public ResponseEntity<ApplicationDTO> createApplication(
                        @RequestBody ApplicationRequestDTO request) {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                ApplicationDTO applicationDTO = applicationService.createApplication(
                                request.getJobId(), email, request.getCvPath(), request.getCoverLetter());

                return ResponseEntity.ok(applicationDTO);
        }

        @Operation(summary = "Get application detail", description = "Retrieve application by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = ApplicationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Application not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ApplicationDTO> getApplication(@PathVariable long id) {
                ApplicationDTO applicationDTO = applicationService.getApplicationById(id);
                return ResponseEntity.ok(applicationDTO);
        }

        @Operation(summary = "Get my applications", description = "Retrieve all applications of current user with pagination")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = ResultPaginationDTO.class)))
        })
        @GetMapping("/my-applications")
        public ResponseEntity<ResultPaginationDTO> getMyApplications(
                        @Parameter(hidden = true) @Filter Specification<Application> spec,
                        @ParameterObject Pageable pageable) {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                User user = userService.handleGetUserByName(email);

                ResultPaginationDTO applications = applicationService.getApplicationsByUser(user.getId(), pageable);
                return ResponseEntity.ok(applications);
        }

        @Operation(summary = "Get my applications by status", description = "Filter current user's applications by status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success")
        })
        @GetMapping("/my-applications/status")
        public ResponseEntity<ResultPaginationDTO> getMyApplicationsByStatus(
                        @RequestParam ApplicationStatus status,
                        Pageable pageable) {
                long userId = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? Long.parseLong(SecurityUtil.getCurrentUserLogin().get())
                                : 0;

                ResultPaginationDTO applications = applicationService.getApplicationsByUserAndStatus(userId, status,
                                pageable);
                return ResponseEntity.ok(applications);
        }

        /**
         * Lấy ứng tuyển cho job (cho nhà tuyển dụng)
         * GET /api/v1/applications/job/{jobId}
         */
        // @GetMapping("/job/{jobId}")
        // public ResponseEntity<RestResponse<Page<ApplicationDTO>>>
        // getApplicationsByJob(
        // @PathVariable long jobId,
        // Pageable pageable) {
        // Page<ApplicationDTO> applications =
        // applicationService.getApplicationsByJob(jobId, pageable);
        // return ResponseEntity
        // .ok(new RestResponse<>(HttpStatus.OK.value(), "Get applications by job
        // successfully", applications));
        // }

        /**
         * Lấy ứng tuyển theo trạng thái của job
         * GET /api/v1/applications/job/{jobId}/status?status=PENDING
         */
        // @GetMapping("/job/{jobId}/status")
        // public ResponseEntity<RestResponse<Page<ApplicationDTO>>>
        // getApplicationsByJobAndStatus(
        // @PathVariable long jobId,
        // @RequestParam ApplicationStatus status,
        // Pageable pageable) {
        // Page<ApplicationDTO> applications =
        // applicationService.getApplicationsByJobAndStatus(jobId, status, pageable);
        // return ResponseEntity
        // .ok(new RestResponse<>(HttpStatus.OK.value(), "Get applications by status
        // successfully", applications));
        // }

        @Operation(summary = "Update application status", description = "Update status of an application (for recruiter/admin)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Updated successfully", content = @Content(schema = @Schema(implementation = ApplicationDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid status"),
                        @ApiResponse(responseCode = "404", description = "Application not found")
        })
        @PutMapping("/{id}/status")
        public ResponseEntity<ApplicationDTO> updateApplicationStatus(
                        @PathVariable long id,
                        @RequestParam ApplicationStatus status,
                        @RequestParam(required = false) String reviewNote) {
                ApplicationDTO applicationDTO = applicationService.updateApplicationStatus(id, status, reviewNote);
                return ResponseEntity.ok(
                                applicationDTO);
        }

        @Operation(summary = "Withdraw application", description = "User withdraws their application")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Withdraw successful"),
                        @ApiResponse(responseCode = "404", description = "Application not found")
        })
        @DeleteMapping("/{id}/withdraw")
        public ResponseEntity<String> withdrawApplication(@PathVariable long id) {
                applicationService.withdrawApplication(id);
                return ResponseEntity.ok(
                                null);
        }

        @Operation(summary = "Delete application", description = "Delete an application")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Application not found")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteApplication(@PathVariable long id) {
                applicationService.deleteApplication(id);
                return ResponseEntity.ok(
                                null);
        }

        @Operation(summary = "Get application statistics", description = "Retrieve application statistics for a job")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = ApplicationStatsDTO.class)))
        })
        @GetMapping("/job/{jobId}/stats")
        public ResponseEntity<ApplicationStatsDTO> getApplicationStats(@PathVariable long jobId) {
                ApplicationStatsDTO stats = applicationService.getApplicationStats(jobId);
                return ResponseEntity.ok(
                                stats);
        }
}