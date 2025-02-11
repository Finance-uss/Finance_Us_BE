package finance_us.finance_us.domain.statistics.controller;

import finance_us.finance_us.domain.statistics.dto.CategoryGoalStatisticsResponse;
import finance_us.finance_us.domain.statistics.dto.CategoryStatisticsResponse;
import finance_us.finance_us.domain.statistics.dto.GoalStatisticsResponse;
import finance_us.finance_us.domain.statistics.service.CategoryStatisticsService;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics/category")
@RequiredArgsConstructor
public class CategoryStatisticsController {
    private final CategoryStatisticsService categoryStatisticsService;

    @GetMapping("/")
    @Operation(summary = "이번 달 카테고리별 차지 비율 조회 API(원형 그래프)")
    public ApiResponse<CategoryStatisticsResponse> getCategoryStatistics(
            @RequestHeader("Authorization") String token,
            @RequestParam Long year,
            @RequestParam Long month,
            @RequestParam String type)
            {
        CategoryStatisticsResponse response = categoryStatisticsService.getCategoryStatistics(token, year, month, type);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/goal-per-total")
    @Operation(summary = "이번 달 목표금액 및 합산 금액 조회 API")
    public ApiResponse<GoalStatisticsResponse> getGoalStatistics(
            @RequestHeader("Authorization") String token,
            @RequestParam Long year,
            @RequestParam Long month,
            @RequestParam String type)
            {
        GoalStatisticsResponse response = categoryStatisticsService.getGoalStatistics(token, year, month, type);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/goal-per-category")
    @Operation(summary = "이번 달 카테고리 별 목표금액 및 합산금액 조회 API")
    public ApiResponse<CategoryGoalStatisticsResponse> getCategoryGoalStatistics(
            @RequestHeader("Authorization") String token,
            @RequestParam Long year,
            @RequestParam Long month,
            @RequestParam String type)
            {
        CategoryGoalStatisticsResponse response = categoryStatisticsService.getCategoryGoalStatistics(token, year, month, type);

        return ApiResponse.onSuccess(response);
    }

}
