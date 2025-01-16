package finance_us.finance_us.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PeriodStatisticsResponse {
    private Long year;
    private String type;
    private List<MonthData> monthlyData;

    @Getter
    @AllArgsConstructor
    public static class MonthData{
        private Long month;
        private Long totalMoney;
    }
}
