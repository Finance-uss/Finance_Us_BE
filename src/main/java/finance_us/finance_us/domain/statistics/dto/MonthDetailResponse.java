package finance_us.finance_us.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MonthDetailResponse {
    private Long year;
    private Long month;
    private String type;
    private Long totalMoney;
    private List<Detail> details;

    @Getter
    @AllArgsConstructor
    public static class Detail{
        private Long day;
        private String title;
        private Long amount;
    }
}
