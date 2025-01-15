package finance_us.finance_us.domain.category.repository;

import finance_us.finance_us.domain.category.entity.MainAsset;
import finance_us.finance_us.domain.category.entity.MainCategory;
import finance_us.finance_us.domain.category.entity.status.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainAssetRepository extends JpaRepository<MainAsset, Long>
{
    @Query(value = "SELECT * FROM main_asset WHERE user_id=:userId;", nativeQuery = true)
    List<MainAsset> findByUserId(@Param("userId") Long userId);

}
