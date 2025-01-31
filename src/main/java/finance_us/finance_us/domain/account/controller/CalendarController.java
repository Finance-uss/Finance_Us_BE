package finance_us.finance_us.domain.account.controller;


import finance_us.finance_us.domain.account.converter.CalendarConverter;
import finance_us.finance_us.domain.account.dto.CalendarDetailResponse;
import finance_us.finance_us.domain.account.dto.CalendarResponse;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.service.CalendarService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    // 가계부 달력 조회
    @GetMapping("/{year}/{month}")
    public ApiResponse<CalendarResponse.CalendarResponseDTO> getCalendar(@PathVariable Integer year, @PathVariable Integer month, Authentication authentication) {
        CalendarResponse.CalendarResponseDTO response = calendarService.getCalendar(year, month, authentication);
        return ApiResponse.onSuccess(response);
    }

    // 가계부 달력 일별 조회
    @GetMapping("/{year}/{month}/{day}")
    public ApiResponse<List<CalendarDetailResponse.CalendarDetailResponseDTO>> getCalendarDetail(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @PathVariable Integer day,
            Authentication authentication) {

        List<Account> accounts = calendarService.getCalendarDetail(year, month, day, authentication);
        return ApiResponse.onSuccess(CalendarConverter.toCalendarDetailResponseDTOList(accounts));
    }

}
