package vn.baymax.fjob.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.baymax.fjob.domain.Job;
import vn.baymax.fjob.domain.Skill;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    // List<Job> findBySkillsInAndNotificationSentFalse(List<Skill> skills);
    @Query("SELECT FROM Job j join j.skills s WHERE j.notificationSent = fasle AND s IN :skills")
    List<Job> findBySkillsAndNotiSendFalse(@Param("skills") List<Skill> skills);

}
