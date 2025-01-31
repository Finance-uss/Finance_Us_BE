package finance_us.finance_us.domain.account.repository;

import finance_us.finance_us.domain.account.entity.AccountCheer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheerRepository extends JpaRepository<AccountCheer, Long> {
    // userId와 accountId로 좋아요 여부 확인
    Optional<AccountCheer> findByUserIdAndAccountId(Long userId, Long accountId);
}