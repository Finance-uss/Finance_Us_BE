package finance_us.finance_us.domain.category.repository;

import finance_us.finance_us.domain.category.entity.SubCategory;
import finance_us.finance_us.domain.category.entity.status.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    // subName으로 SubCategory 조회
    Optional<SubCategory> findBySubName(String subName);
    // userId 와 CategoryType으로 조회
    @Query(value = "SELECT * FROM sub_category WHERE user_id=:userId AND main_category_id=:mainCategoryId;", nativeQuery = true)
    public List<SubCategory> findByUserIdAndMainCategoryId(@Param("userId") Long userId, @Param("mainCategoryId") Long mainCategoryId);

    @Query("SELECT s FROM SubCategory s WHERE s.user.id = :userId")
    List<SubCategory> findByUserId(Long userId);
    // goal이 null이 아닌 값만 불러오기
    @Query(value = "SELECT s.* FROM main_category m INNER JOIN sub_category s ON m.id=s.main_category_id " +
                   "WHERE m.user_id=:userId AND m.category_type=:categoryType AND s.goal >= 0;", nativeQuery = true)
    public List<SubCategory> findByGoal(@Param("userId") Long userId, @Param("categoryType") CategoryType categoryType);

    // 카테고리 타입으로 찾기
    @Query(value = "SELECT s.* FROM main_category m INNER JOIN sub_category s ON m.id=s.main_category_id " +
            "WHERE m.user_id=:userId AND m.category_type=:categoryType;", nativeQuery = true)
    public List<SubCategory> findByType(@Param("userId") Long userId, @Param("categoryType") CategoryType categoryType);
}
