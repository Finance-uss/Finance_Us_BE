package finance_us.finance_us.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CategoryGoalStatisticsResponse {
    private Long year;
    private Long month;
    private String type;
    private List<CategoryGoalData> categories;

    @Getter
    @AllArgsConstructor
    public static class CategoryGoalData {
        private String mainCategory;
        private Long totalSpent;
        private Long goal;
        private Integer percentage;
    }
}
