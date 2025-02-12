package finance_us.finance_us.domain.statistics.repository;

import finance_us.finance_us.domain.statistics.entity.PeriodStatistics;
import finance_us.finance_us.domain.statistics.entity.status.Type;
import finance_us.finance_us.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodStatisticsRepository extends JpaRepository<PeriodStatistics, Long> {

    @Modifying
    @Query("UPDATE PeriodStatistics ps " +
            "SET ps.totalMoney = :totalMoney " +
            "WHERE ps.year = :year AND ps.month = :month AND ps.type = :type AND ps.user.Id = :userId")
    void updateTotalMoney(@Param("year") Long year,
                          @Param("month") Long month,
                          @Param("type") Type type,
                          @Param("totalMoney") Long totalMoney,
                          @Param("userId") Long userId);

    /**
     * ✅ 특정 연도의 통계 목록 조회
     */
    List<PeriodStatistics> findByYearAndTypeAndUserId(Long year, Type type, Long userId);

    /**
     * ✅ 특정 연도 + 월의 통계 데이터 단일 조회
     */
    Optional<PeriodStatistics> findByYearAndMonthAndTypeAndUserId(Long year, Long month, Type type, Long userId);

    @Query("SELECT ps FROM PeriodStatistics ps WHERE " +
            "(ps.year > :startYear OR (ps.year = :startYear AND ps.month >= :startMonth)) " +
            "AND (ps.year < :endYear OR (ps.year = :endYear AND ps.month <= :endMonth)) " +
            "AND ps.type = :type AND ps.user.Id = :userId " +
            "ORDER BY ps.year, ps.month")
    List<PeriodStatistics> findByDateRangeAndTypeAndUserId(@Param("startYear") Long startYear,
                                                           @Param("startMonth") Long startMonth,
                                                           @Param("endYear") Long endYear,
                                                           @Param("endMonth") Long endMonth,
                                                           @Param("type") Type type,
                                                           @Param("userId") Long userId);

}
