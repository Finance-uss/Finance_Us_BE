package finance_us.finance_us.domain.account.repository;

import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.entity.status.AccountType;
import finance_us.finance_us.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT a FROM Account a WHERE a.date BETWEEN :startDate AND :endDate AND a.accountType = :accountType AND a.user = :user")
    List<Account> findBytDateBetweenAndAccountType(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("accountType") AccountType accountType,
            @Param("user")User user
    );

}
