package vn.baymax.fjob.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import vn.baymax.fjob.domain.Job;
import vn.baymax.fjob.repository.JobRepository;
import vn.baymax.fjob.util.TextUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobToolService {

    private final JobRepository jobRepository;

    public JobToolService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Tool(description = "Search jobs by skill")
    public List<Job> searchJobs(String skill) {
        return jobRepository.findBySkills_NameContainingIgnoreCase(skill);
    }

    @Tool(description = "Search jobs by location")
    public List<Job> searchJobsByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    @Tool(description = """
            Search jobs using filters like skill, location, salary and level.
            skill: programming skill like Java, Python, React
            location: city or country
            minSalary: minimum salary expected
            level: job level like INTERN, FRESHER, JUNIOR, MIDDLE, SENIOR
            """)
    public List<Job> searchJobsAdvanced(
            String skill,
            String location,
            Double minSalary,
            String level) {

        List<Job> jobs = jobRepository.findAll();

        return jobs.stream()

                // filter skill
                .filter(job -> skill == null ||
                        job.getSkills().stream()
                                .anyMatch(s -> s.getName()
                                        .toLowerCase()
                                        .contains(skill.toLowerCase())))

                // filter location
                .filter(job -> location == null ||
                        TextUtil.normalize(job.getLocation())
                                .contains(TextUtil.normalize(location)))

                // filter salary
                .filter(job -> minSalary == null ||
                        job.getSalary() >= minSalary)

                // filter level
                .filter(job -> level == null ||
                        job.getLevel().name()
                                .equalsIgnoreCase(level))

                .collect(Collectors.toList());
    }

}