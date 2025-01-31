package finance_us.finance_us.domain.statistics.controller;

import finance_us.finance_us.domain.statistics.dto.CategoryGoalStatisticsResponse;
import finance_us.finance_us.domain.statistics.dto.CategoryStatisticsResponse;
import finance_us.finance_us.domain.statistics.dto.GoalStatisticsResponse;
import finance_us.finance_us.domain.statistics.service.CategoryStatisticsService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics/category")
@RequiredArgsConstructor
public class CategoryStatisticsController {
    private final CategoryStatisticsService categoryStatisticsService;

    @GetMapping("/")
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
