package finance_us.finance_us.domain.category.repository;

import finance_us.finance_us.domain.category.entity.SubCategory;
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


}
