package finance_us.finance_us.domain.category.repository.category;

import finance_us.finance_us.domain.category.entity.category.SubCategory;
import finance_us.finance_us.domain.category.entity.category.status.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long>
{

}
