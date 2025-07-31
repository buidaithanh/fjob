package vn.baymax.fjob.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import vn.baymax.fjob.domain.Company;
import vn.baymax.fjob.domain.Job;
import vn.baymax.fjob.domain.Resume;
import vn.baymax.fjob.domain.User;
import vn.baymax.fjob.dto.response.ResCreateResumeDTO;
import vn.baymax.fjob.dto.response.ResGetResumeDTO;
import vn.baymax.fjob.dto.response.ResUpdateResumeDTO;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.service.ResumeService;
import vn.baymax.fjob.service.UserService;
import vn.baymax.fjob.util.SecurityUtil;
import vn.baymax.fjob.util.annotation.ApiMessage;
import vn.baymax.fjob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService, UserService userService,
            FilterSpecificationConverter filterSpecificationConverter, FilterBuilder filterBuilder) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
    }

    @PostMapping("/resumes")
    @ApiMessage("create a new resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@RequestBody Resume resume) throws IdInvalidException {
        boolean isIdExist = this.resumeService.checkExistResumeByUserAndJob(resume);
        if (!isIdExist) {
            throw new IdInvalidException("userid / jobid is not exist");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("update status resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.getResumeById(resume.getId());
        if (resumeOptional.isEmpty()) {
            throw new IdInvalidException("resume with id " + resume.getId() + " not exist");
        }

        Resume reqResume = resumeOptional.get();
        reqResume.setStatus(resume.getStatus());
        return ResponseEntity.ok().body(this.resumeService.update(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("delete a resume by id")
    public ResponseEntity<Void> deleteResume(@PathVariable long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.getResumeById(id);
        if (resumeOptional.isEmpty()) {
            throw new IdInvalidException("resume with id " + id + " not exist");
        }

        this.resumeService.delete(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("get a resume by id")
    public ResponseEntity<ResGetResumeDTO> getResume(@PathVariable long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.getResumeById(id);
        if (resumeOptional.isEmpty()) {
            throw new IdInvalidException("resume with id " + id + " not exist");
        }

        return ResponseEntity.ok().body(this.resumeService.getResume(resumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("get all resume")
    public ResponseEntity<ResultPaginationDTO> getAllResume(
            @Filter Specification<Resume> spec,
            Pageable pageable) {
        List<Long> arrJobs = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByName(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null) {
                    arrJobs = companyJobs.stream().map(x -> x.getId()).collect(Collectors.toList());

                }
            }
        }
        Specification<Resume> jobSpec = filterSpecificationConverter
                .convert(filterBuilder.field("job").in(filterBuilder.input(arrJobs)).get());
        Specification<Resume> finalSpec = jobSpec.and(spec);
        return ResponseEntity.ok(this.resumeService.getAll(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("get list resume by user")
    public ResponseEntity<ResultPaginationDTO> getResumeByUser(
            Pageable pageable) {
        return ResponseEntity.ok(this.resumeService.getResumeByUser(pageable));
    }
}
