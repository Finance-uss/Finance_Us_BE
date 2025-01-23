package finance_us.finance_us.domain.account.converter;

import finance_us.finance_us.domain.account.dto.CalendarDetailResponse;
import finance_us.finance_us.domain.account.dto.CalendarResponse;
import finance_us.finance_us.domain.account.entity.Account;

import java.util.List;
import java.util.stream.Collectors;

public class CalendarConverter {
    // 가계부 달력 조회
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

    // 가계부 달력 일별 조회
    public static List<CalendarDetailResponse.CalendarDetailResponseDTO> toCalendarDetailResponseDTOList(List<Account> accounts) {
        return accounts.stream()
                .map(account -> new CalendarDetailResponse.CalendarDetailResponseDTO(
                        account.getId(),
                        account.getScore(),
                        account.getTitle(),
                        account.getAmount(),
                        account.getSubCategory().getSubName(),
                        account.getImageUrl()
                ))
                .collect(Collectors.toList());
    }
}
