package finance_us.finance_us.domain.category.repository;

import finance_us.finance_us.domain.category.entity.MainAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainAssetRepository extends JpaRepository<MainAsset, Long>
{


}
