package finance_us.finance_us.domain.statistics.repository;

import finance_us.finance_us.domain.statistics.entity.PeriodStatistics;
import finance_us.finance_us.domain.statistics.entity.status.Type;
import finance_us.finance_us.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeriodStatisticsRepository extends JpaRepository<PeriodStatistics, Long> {

    @Modifying
    @Query("UPDATE PeriodStatistics ps " +
            "SET ps.totalMoney = :totalMoney " +
            "WHERE ps.year = :year AND ps.month = :month AND ps.type = :type AND ps.user = :user")
    void updateTotalMoney(Long year, Long month, Type type, Long totalMoney, User user);

    List<PeriodStatistics> findByYearAndType(Long year, Type type);

    List<PeriodStatistics> findByYearAndMonthAndType(Long year, Long month, Type type);
}
