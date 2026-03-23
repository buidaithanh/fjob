package vn.baymax.fjob.repository;

import vn.baymax.fjob.domain.Application;
import vn.baymax.fjob.domain.Application.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

        Optional<Application> findByJobIdAndUserId(long jobId, long userId);

        Page<Application> findByUserIdOrderByCreatedAtDesc(long userId, Pageable pageable);

        Page<Application> findByJobIdOrderByCreatedAtDesc(long jobId, Pageable pageable);

        Page<Application> findByUserIdAndStatusOrderByCreatedAtDesc(
                        long userId, ApplicationStatus status, Pageable pageable);

        Page<Application> findByJobIdAndStatusOrderByCreatedAtDesc(
                        long jobId, ApplicationStatus status, Pageable pageable);

        long countByJobIdAndStatus(long jobId, ApplicationStatus status);

        long countByJobId(long jobId);

        long countByUserId(long userId);

        boolean existsByJobIdAndUserId(long jobId, long userId);

        Page<Application> findByUserId(Long userId, Pageable pageable);

        Page<Application> findByUserIdAndStatus(Long userId, ApplicationStatus status, Pageable pageable);
}