package vn.baymax.fjob.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.baymax.fjob.domain.Company;
import vn.baymax.fjob.domain.Job;
import vn.baymax.fjob.domain.Skill;
import vn.baymax.fjob.dto.response.ResCreateJobDTO;
import vn.baymax.fjob.dto.response.ResUpdateJobDTO;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.repository.CompanyRepository;
import vn.baymax.fjob.repository.JobRepository;
import vn.baymax.fjob.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResCreateJobDTO createJob(Job job) {
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(sk -> sk.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            Optional<Company> companyOpt = this.companyRepository.findById(job.getCompany().getId());
            if (!companyOpt.isPresent()) {
                throw new RuntimeException("Company with ID " + job.getCompany().getId() + " does not exist");
            }
            job.setCompany(companyOpt.get());
            job.setSkills(dbSkills);
        }
        Job currentJob = this.jobRepository.save(job);

        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setQuantity(currentJob.getQuantity());
        dto.setSalary(currentJob.getSalary());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setAcctive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(sk -> sk.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public Optional<Job> getJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResUpdateJobDTO updateJob(Job job) {
        // check skill
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(sk -> sk.getId())
                    .collect(Collectors.toList());

            List<Skill> skills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(skills);
        }
        Job currentJob = this.jobRepository.save(job);

        // mapping to response update job
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setQuantity(currentJob.getQuantity());
        dto.setSalary(currentJob.getSalary());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setAcctive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getCreatedAt());
        dto.setUpdatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(sk -> sk.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public void deleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageJob.getContent());
        return rs;
    }

}
