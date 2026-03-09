package vn.baymax.fjob.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.baymax.fjob.domain.Skill;
import vn.baymax.fjob.domain.Subscriber;

@Repository
public interface SubcriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    boolean existsByEmail(String email);

    Subscriber findByEmail(String email);

    @Query("SELECT DISTINCT s FROM Subscriber s JOIN s.skills sk WHERE sk IN :skills")
    List<Subscriber> findSubscribersBySkills(@Param("skills") List<Skill> skills);
}
