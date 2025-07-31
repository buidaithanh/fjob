package vn.baymax.fjob.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.baymax.fjob.domain.Job;
import vn.baymax.fjob.dto.response.ResCreateJobDTO;
import vn.baymax.fjob.dto.response.ResUpdateJobDTO;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.service.JobService;
import vn.baymax.fjob.util.annotation.ApiMessage;
import vn.baymax.fjob.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<ResCreateJobDTO> createJob(@RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.createJob(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("update job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@RequestBody Job job) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.getJobById(job.getId());
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("job not foud");
        }
        return ResponseEntity.ok(this.jobService.updateJob(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete job by id")
    public ResponseEntity<Void> deleteJob(@PathVariable long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.getJobById(id);
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("job not found");
        }
        this.jobService.deleteJob(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("get job by id")
    public ResponseEntity<Job> getJobById(@PathVariable long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.getJobById(id);
        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("job not found");
        }

        return ResponseEntity.ok().body(jobOptional.get());
    }

    @GetMapping("/jobs")
    @ApiMessage("get all job")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable) throws IdInvalidException {

        return ResponseEntity.ok().body(this.jobService.getAllJobs(spec, pageable));
    }

}
