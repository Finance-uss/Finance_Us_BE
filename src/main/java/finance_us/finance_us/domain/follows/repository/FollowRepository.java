package finance_us.finance_us.domain.follows.repository;

import finance_us.finance_us.domain.follows.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

}

