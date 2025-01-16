package finance_us.finance_us.domain.category.repository;

import finance_us.finance_us.domain.category.entity.MainCategory;
import finance_us.finance_us.domain.category.entity.status.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long>
{
    @Query(value = "SELECT * FROM main_category WHERE user_id=:userId AND category_type=:type;", nativeQuery = true)
    List<MainCategory> findByUserIdAndCategoryType(@Param("userId") Long userId, @Param("type") CategoryType type);
}
