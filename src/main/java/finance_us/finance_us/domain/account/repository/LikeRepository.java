package finance_us.finance_us.domain.account.repository;

import finance_us.finance_us.domain.account.entity.AccountLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<AccountLike, Long> {
    // userId와 accountId로 좋아요 여부 확인
    Optional<AccountLike> findByUserIdAndAccountId(Long userId, Long accountId);
}
