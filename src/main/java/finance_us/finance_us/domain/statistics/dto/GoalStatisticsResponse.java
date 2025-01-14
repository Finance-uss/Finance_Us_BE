package finance_us.finance_us.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalStatisticsResponse {
    private Long year;
    private Long month;
    private String type;
    private Long totalSpent;
    private Long goal;
    private Integer percentage;
}
