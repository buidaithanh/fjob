package vn.baymax.fjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.baymax.fjob.domain.Subscriber;

@Repository
public interface SubcriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    boolean existsByEmail(String email);

    Subscriber findByEmail(String email);
}
