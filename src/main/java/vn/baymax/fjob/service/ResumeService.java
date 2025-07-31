package vn.baymax.fjob.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.baymax.fjob.domain.Job;
import vn.baymax.fjob.domain.Resume;
import vn.baymax.fjob.domain.User;
import vn.baymax.fjob.dto.response.ResCreateResumeDTO;
import vn.baymax.fjob.dto.response.ResGetResumeDTO;
import vn.baymax.fjob.dto.response.ResUpdateResumeDTO;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.repository.JobRepository;
import vn.baymax.fjob.repository.ResumeRepository;
import vn.baymax.fjob.repository.UserRepository;
import vn.baymax.fjob.util.SecurityUtil;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
            JobRepository jobRepository, FilterParser filterParser,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    public Optional<Resume> getResumeById(long id) {
        return this.resumeRepository.findById(id);
    }

    public ResGetResumeDTO getResume(Resume resume) {
        ResGetResumeDTO res = ResGetResumeDTO.builder()
                .id(resume.getId())
                .email(resume.getEmail())
                .url(resume.getUrl())
                .status(resume.getStatus())
                .companyName(resume.getJob().getCompany().getName())
                .createdAt(resume.getCreatedAt())
                .createdBy(resume.getCreatedBy())
                .updatedAt(resume.getUpdatedAt())
                .updatedBy(resume.getUpdatedBy())
                .user(
                        ResGetResumeDTO.UserResume.builder()
                                .id(resume.getUser().getId())
                                .name(resume.getUser().getName()).build())
                .job(
                        ResGetResumeDTO.JobResume.builder()
                                .id(resume.getJob().getId())
                                .name(resume.getJob().getName()).build())
                .build();

        return res;
    }

    public boolean checkExistResumeByUserAndJob(Resume resume) {
        // check user id
        if (resume.getUser() == null)
            return false;
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty())
            return false;

        // check job id
        if (resume.getJob() == null)
            return false;
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty())
            return false;

        return true;

    }

    public ResCreateResumeDTO createResume(Resume resume) {
        resume = this.resumeRepository.save(resume);
        return ResCreateResumeDTO.builder()
                .id(resume.getId())
                .createdAt(resume.getCreatedAt())
                .createdBy(resume.getCreatedBy())
                .build();
    }

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        return ResUpdateResumeDTO.builder()
                .updatedAt(resume.getUpdatedAt())
                .updatedBy(resume.getUpdatedBy())
                .build();
    }

    public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResultPaginationDTO getAll(Specification<Resume> spec,
            Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);
        List<ResGetResumeDTO> listResume = pageResume.getContent().stream()
                .map(item -> this.getResume(item))
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }

    public ResultPaginationDTO getResumeByUser(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : " ";
        FilterNode node = filterParser.parse("email'" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);

        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);
        List<ResGetResumeDTO> listResume = pageResume.getContent().stream()
                .map(item -> this.getResume(item))
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }
}
