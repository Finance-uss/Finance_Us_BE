package finance_us.finance_us.domain.category.repository.asset;

import finance_us.finance_us.domain.category.entity.asset.MainAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainAssetRepository extends JpaRepository<MainAsset, Long>
{


}
