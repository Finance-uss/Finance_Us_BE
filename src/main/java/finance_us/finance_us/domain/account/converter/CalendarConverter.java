package finance_us.finance_us.domain.account.converter;

import finance_us.finance_us.domain.account.dto.CalendarResponse;

import java.util.List;
public class CalendarConverter {
    public static CalendarResponse.CalendarResponseDTO toCalendarResponseDTO(
            Double totalScore, Double totalExpense, Double totalIncome, List<CalendarResponse.CalendarDTO> calendar
            ) {
        return CalendarResponse.CalendarResponseDTO.builder()
                .totalScore(totalScore)
                .totalExpense(totalExpense)
                .totalIncome(totalIncome)
                 .calendar(calendar)
                .build();
    }
}
