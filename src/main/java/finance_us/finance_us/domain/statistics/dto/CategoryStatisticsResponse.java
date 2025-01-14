package finance_us.finance_us.domain.statistics.dto;

import finance_us.finance_us.domain.category.entity.MainCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryStatisticsResponse {
    private Long year;
    private Long month;
    private String type;
    private List<CategoryData> categories;

    @Getter
    @AllArgsConstructor
    public static class CategoryData{
        private String mainCategory;
        private Long totalSpent;
        private Integer percentage;

        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }
    }
}
