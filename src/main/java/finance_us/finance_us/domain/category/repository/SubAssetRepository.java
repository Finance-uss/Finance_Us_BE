package finance_us.finance_us.domain.category.repository;

import finance_us.finance_us.domain.category.entity.SubAsset;
import finance_us.finance_us.domain.category.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubAssetRepository  extends JpaRepository<SubAsset, Long> {
    // subName으로 SubAsset 조회
    Optional<SubAsset> findBySubName(String subName);
    // userId 와 CategoryType으로 조회
    @Query(value = "SELECT * FROM sub_category WHERE user_id=:userId;", nativeQuery = true)
    public List<SubAsset> findByUserId(@Param("userId") Long userId);

}
