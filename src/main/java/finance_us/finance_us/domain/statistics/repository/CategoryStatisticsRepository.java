package finance_us.finance_us.domain.statistics.repository;

import finance_us.finance_us.domain.statistics.entity.CategoryStatistics;
import finance_us.finance_us.domain.statistics.entity.status.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryStatisticsRepository extends JpaRepository<CategoryStatistics, Long> {
    @Modifying
    @Query("UPDATE CategoryStatistics cs " +
            "SET cs.totalGoal = :totalGoal,  cs.updatedAt = CURRENT_TIMESTAMP  " +
            "WHERE cs.mainCategory.id = :mainCategoryId " +
            " AND cs.user.Id = :userId" +
            " AND cs.year = :year" +
            " AND cs.month = :month" +
            " AND cs.type = :type")
    void updateGoalWithTimeStamp(Long userId, Long mainCategoryId, Long year, Long month, String type, Long totalGoal);

    @Modifying
    @Query("UPDATE CategoryStatistics cs " +
            "SET cs.totalGoal = :totalMoney,  cs.updatedAt = CURRENT_TIMESTAMP  " +
            "WHERE cs.mainCategory.id = :mainCategoryId " +
            " AND cs.user.Id = :userId" +
            " AND cs.year = :year" +
            " AND cs.month = :month" +
            " AND cs.type = :type")
    void updateTotalWithTimeStamp(Long userId, Long mainCategoryId, Long year, Long month, String type, Long totalMoney);

    List<CategoryStatistics> findByYearAndMonthAndType(Long year, Long month, Type type);
}
