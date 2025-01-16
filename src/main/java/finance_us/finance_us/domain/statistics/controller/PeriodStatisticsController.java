package finance_us.finance_us.domain.statistics.controller;

import finance_us.finance_us.domain.account.entity.status.AccountType;
import finance_us.finance_us.domain.statistics.dto.MonthDetailResponse;
import finance_us.finance_us.domain.statistics.dto.PeriodStatisticsResponse;
import finance_us.finance_us.domain.statistics.service.PeriodStatisticsService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics/period")
@RequiredArgsConstructor
public class PeriodStatisticsController {
    private final PeriodStatisticsService periodStatisticsService;

    @GetMapping("/")
    public ApiResponse<PeriodStatisticsResponse> getYearlyStatistics(
            @RequestParam Long year,
            @RequestParam String type)
        //  @RequestHeader("Authorization") String accessToken
    {
        PeriodStatisticsResponse response = periodStatisticsService.getYearlyStatistics(year, type);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/details")
    public ApiResponse<MonthDetailResponse> getMonthlyDetails(
            @RequestParam Long year,
            @RequestParam Long month,
            @RequestParam AccountType type)
            //@RequestHeader("Authorization") String accessToken
    {
        MonthDetailResponse response = periodStatisticsService.getMonthlyDetail(year, month, type);

        return ApiResponse.onSuccess(response);
    }
}
