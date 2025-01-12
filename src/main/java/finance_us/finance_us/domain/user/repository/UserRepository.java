package finance_us.finance_us.domain.user.repository;

import finance_us.finance_us.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    User findByEmailAndPassword(String email, String password);

    User findById(Long id);
    Optional<User> findByName(String name);
}

