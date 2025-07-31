package vn.baymax.fjob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.baymax.fjob.domain.Company;
import vn.baymax.fjob.domain.User;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findById(long id);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String refreshToken, String email);

    List<User> findByCompany(Company company);
}
