package finance_us.finance_us.domain.account.controller;


import finance_us.finance_us.domain.account.converter.CalendarConverter;
import finance_us.finance_us.domain.account.dto.CalendarDetailResponse;
import finance_us.finance_us.domain.account.dto.CalendarResponse;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.service.CalendarService;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar API", description = "달력 관련 API")
public class CalendarController {
    private final CalendarService calendarService;

    // 가계부 달력 조회
    @GetMapping("/{year}/{month}")
    @Operation(summary = "달력 조회 API")
    public ApiResponse<CalendarResponse.CalendarResponseDTO> getCalendar(@PathVariable Integer year, @PathVariable Integer month,@RequestHeader("Authorization") String token) {
        CalendarResponse.CalendarResponseDTO response = calendarService.getCalendar(year, month, token);
        return ApiResponse.onSuccess(response);
    }

    // 가계부 달력 일별 조회
    @GetMapping("/{year}/{month}/{day}")
    @Operation(summary = "달력 일별 조회 API")
    public ApiResponse<List<CalendarDetailResponse.CalendarDetailResponseDTO>> getCalendarDetail(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @PathVariable Integer day,
            @RequestHeader("Authorization") String token) {

        List<Account> accounts = calendarService.getCalendarDetail(year, month, day, token);
        return ApiResponse.onSuccess(CalendarConverter.toCalendarDetailResponseDTOList(accounts));
    }

}
