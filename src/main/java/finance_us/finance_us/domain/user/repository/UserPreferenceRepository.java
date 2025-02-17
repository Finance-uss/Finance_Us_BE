package finance_us.finance_us.domain.user.repository;

import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserPreferenceRepository  extends JpaRepository<UserPreference,Long>
{
    @Query(value="SELECT * FROM user_preference WHERE user_id=:userId;", nativeQuery = true)
    Optional<UserPreference> findByUserId(@Param("userId") Long userId);

}
