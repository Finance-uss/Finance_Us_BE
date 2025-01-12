package finance_us.finance_us.domain.user.repository;

import finance_us.finance_us.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
    Boolean existsByEmail(String email);
    User findByEmailAndPassword(String email, String password);

    User findById(Long id);

}
