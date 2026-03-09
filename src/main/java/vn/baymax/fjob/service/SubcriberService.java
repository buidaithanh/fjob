package vn.baymax.fjob.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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

    @Scheduled(cron = "0 0/1 * * * ?") // 15 min
    @Transactional
    public void sendSubscribersEmailJobs() {
        List<Job> newJobs = this.jobRepository.findByNotificationSentFalse();
        if (newJobs.isEmpty()) {
            System.out.println("Không có job mới nào, không cần gửi email.");
            return;
        }
        // each subscriber will have list job
        Map<Subscriber, List<Job>> jobsPerSubscriber = new HashMap<>();

        for (Job job : newJobs) {
            // Với mỗi job, tìm tất cả subscriber quan tâm đến các skill của job đó
            List<Subscriber> interestedSubscribers = this.subcriberRepository.findSubscribersBySkills(job.getSkills());

            // Gom job này vào danh sách của những subscriber quan tâm
            for (Subscriber sub : interestedSubscribers) {
                jobsPerSubscriber.computeIfAbsent(sub, k -> new ArrayList<>()).add(job);
            }
        }

        for (Map.Entry<Subscriber, List<Job>> entry : jobsPerSubscriber.entrySet()) {
            Subscriber subscriber = entry.getKey();
            List<Job> relevantJobs = entry.getValue();

            List<RestEmailJob> emailJobsDto = relevantJobs.stream()
                    .map(this::mappingJobToSendEmail)
                    .collect(Collectors.toList());

            this.emailService.sendEmailFromTemplateSync(
                    subscriber.getEmail(),
                    "Cơ hội việc làm HOT đang chờ bạn!",
                    "job",
                    subscriber.getName(),
                    emailJobsDto);
        }
        newJobs.forEach(job -> job.setNotificationSent(true));
        this.jobRepository.saveAll(newJobs);
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
