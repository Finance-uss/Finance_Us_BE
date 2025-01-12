package finance_us.finance_us.domain.category.repository;

import finance_us.finance_us.domain.category.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    // subName으로 SubCategory 조회
    Optional<SubCategory> findBySubName(String subName);
}
