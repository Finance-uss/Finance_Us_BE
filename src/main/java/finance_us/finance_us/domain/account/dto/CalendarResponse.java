package finance_us.finance_us.domain.account.dto;

import lombok.*;

import java.util.List;

public class CalendarResponse {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalendarResponseDTO {
        private Double totalScore;
        private Double totalExpense;
        private Double totalIncome;
        private List<CalendarDTO> calendar;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalendarDTO {
        private String date;
    }
}
