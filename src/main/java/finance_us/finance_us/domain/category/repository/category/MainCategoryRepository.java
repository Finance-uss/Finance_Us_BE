package finance_us.finance_us.domain.category.repository.category;

import finance_us.finance_us.domain.category.entity.category.MainCategory;
import finance_us.finance_us.domain.category.entity.category.status.CategoryType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long>
{
    @Query(value = "SELECT * FROM main_category WHERE user_id=:userId AND category_type=:type;", nativeQuery = true)
    List<MainCategory> findByUserIdAndCategoryType(@Param("userId") Long userId, @Param("type") CategoryType type);

    @Query(value = "DELETE FROM main_category WHERE user_id=:userId AND category_type=:type;", nativeQuery = true)
    List<MainCategory> deleteByUserIdAndCategoryType(@Param("userId") Long userId, @Param("type") CategoryType type);

}
