package vn.baymax.fjob.service;

import vn.baymax.fjob.domain.Application;
import vn.baymax.fjob.domain.Application.ApplicationStatus;
import vn.baymax.fjob.dto.request.ApplicationDTO;
import vn.baymax.fjob.dto.request.ApplicationStatsDTO;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.domain.Job;
import vn.baymax.fjob.domain.User;
import vn.baymax.fjob.repository.ApplicationRepository;
import vn.baymax.fjob.repository.JobRepository;
import vn.baymax.fjob.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ApplicationDTO createApplication(long jobId, String email, String cvPath, String coverLetter) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (applicationRepository.existsByJobIdAndUserId(jobId, user.getId())) {
            throw new RuntimeException("You have already applied for this job");
        }

        Application application = Application.builder()
                .job(job)
                .user(user)
                .cvPath(cvPath)
                .coverLetter(coverLetter)
                .status(ApplicationStatus.PENDING).build();

        application = applicationRepository.save(application);

        // emailService.sendApplicationConfirmation(user.getEmail(),
        // job.getDescription());

        return mapToDTO(application);
    }

    public ApplicationDTO getApplicationById(long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return mapToDTO(application);
    }

    public ResultPaginationDTO getApplicationsByUser(long userId, Pageable pageable) {
        Page<Application> pageApplication = this.applicationRepository.findByUserId(userId, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageApplication.getTotalPages());
        mt.setTotal(pageApplication.getTotalElements());

        rs.setMeta(mt);

        List<ApplicationDTO> listAppication = pageApplication.getContent()
                .stream().map(item -> this.mapToDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listAppication);
        return rs;
    }

    public ResultPaginationDTO getApplicationsByUserAndStatus(
            long userId, ApplicationStatus status, Pageable pageable) {
        Page<Application> applications = applicationRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(applications.getTotalPages());
        mt.setTotal(applications.getTotalElements());

        rs.setMeta(mt);

        List<ApplicationDTO> listAppication = applications.getContent()
                .stream().map(item -> this.mapToDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listAppication);
        return rs;
    }

    public Page<ApplicationDTO> getApplicationsByJob(long jobId, Pageable pageable) {
        Page<Application> applications = applicationRepository
                .findByJobIdOrderByCreatedAtDesc(jobId, pageable);
        return applications.map(this::mapToDTO);
    }

    public Page<ApplicationDTO> getApplicationsByJobAndStatus(
            long jobId, ApplicationStatus status, Pageable pageable) {
        Page<Application> applications = applicationRepository
                .findByJobIdAndStatusOrderByCreatedAtDesc(jobId, status, pageable);
        return applications.map(this::mapToDTO);
    }

    public ApplicationDTO updateApplicationStatus(long applicationId, ApplicationStatus newStatus, String reviewNote) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(newStatus);
        application.setReviewNote(reviewNote);

        application = applicationRepository.save(application);

        // emailService.sendApplicationStatusUpdate(
        // application.getApplicant().getEmail(),
        // application.getJob().getDescription(),
        // newStatus.toString());

        return mapToDTO(application);
    }

    public void withdrawApplication(long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(ApplicationStatus.WITHDRAWN);

        applicationRepository.save(application);
    }

    public void deleteApplication(long applicationId) {
        applicationRepository.deleteById(applicationId);
    }

    public ApplicationStatsDTO getApplicationStats(long jobId) {
        long total = applicationRepository.countByJobId(jobId);
        long pending = applicationRepository.countByJobIdAndStatus(jobId, ApplicationStatus.PENDING);
        long reviewing = applicationRepository.countByJobIdAndStatus(jobId, ApplicationStatus.REVIEWING);
        long accepted = applicationRepository.countByJobIdAndStatus(jobId, ApplicationStatus.ACCEPTED);
        long rejected = applicationRepository.countByJobIdAndStatus(jobId, ApplicationStatus.REJECTED);

        ApplicationStatsDTO stats = new ApplicationStatsDTO();
        stats.setJobId(jobId);
        stats.setTotalApplications(total);
        stats.setPendingCount(pending);
        stats.setReviewingCount(reviewing);
        stats.setAcceptedCount(accepted);
        stats.setRejectedCount(rejected);

        return stats;
    }

    private ApplicationDTO mapToDTO(Application application) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(application.getId());
        dto.setJobId(application.getJob().getId());
        dto.setJobTitle(application.getJob().getDescription());
        dto.setApplicantId(application.getUser().getId());
        dto.setApplicantName(application.getUser().getName());
        dto.setApplicantEmail(application.getUser().getEmail());
        dto.setStatus(application.getStatus());
        dto.setCvPath(application.getCvPath());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setReviewNote(application.getReviewNote());

        return dto;
    }
}