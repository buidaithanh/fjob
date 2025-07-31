package vn.baymax.fjob.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import vn.baymax.fjob.domain.Job;
import vn.baymax.fjob.domain.Skill;
import vn.baymax.fjob.domain.Subscriber;
import vn.baymax.fjob.dto.response.RestEmailJob;
import vn.baymax.fjob.repository.JobRepository;
import vn.baymax.fjob.repository.SkillRepository;
import vn.baymax.fjob.repository.SubcriberRepository;

@Service
public class SubcriberService {
    private final SubcriberRepository subcriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubcriberService(SubcriberRepository subcriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subcriberRepository = subcriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean isExistsByEmail(String email) {
        return this.subcriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber subscriber) {
        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills().stream()
                    .map(s -> s.getId()).collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(skills);
        }
        return this.subcriberRepository.save(subscriber);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> subOptional = this.subcriberRepository.findById(id);
        if (subOptional.isPresent()) {
            return subOptional.get();
        }
        return null;
    }

    public Subscriber update(Subscriber subsDB, Subscriber subscriber) {
        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills().stream()
                    .map(s -> s.getId()).collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(skills);
        }
        return this.subcriberRepository.save(subsDB);
    }

    @Scheduled(fixedRate = 100000)
    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subcriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsAndNotiSendFalse(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<RestEmailJob> arr = listJobs.stream()
                                .map(job -> this.mappingJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(sub.getEmail(),
                                "Co hoi viec lam hot dang cho ban",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    private RestEmailJob mappingJobToSendEmail(Job job) {
        List<Skill> skills = job.getSkills();
        List<RestEmailJob.SkillEmail> s = skills.stream().map(
                skill -> new RestEmailJob.SkillEmail(skill.getName())).collect(Collectors.toList());
        RestEmailJob res = RestEmailJob.builder()
                .name(job.getName())
                .salary(job.getSalary())
                .company(new RestEmailJob.CompanyEmail(job.getCompany().getName()))
                .skills(s)
                .build();

        return res;
    }

    public Subscriber findByEmail(String email) {
        return this.subcriberRepository.findByEmail(email);
    }

}
