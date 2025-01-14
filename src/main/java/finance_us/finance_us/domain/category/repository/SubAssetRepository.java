package finance_us.finance_us.domain.category.repository;

import finance_us.finance_us.domain.category.entity.SubAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubAssetRepository  extends JpaRepository<SubAsset, Long> {
    // subName으로 SubAsset 조회
    Optional<SubAsset> findBySubName(String subName);
}
