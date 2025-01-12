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
            @RequestParam Long year,
            @RequestParam Long month,
            @RequestParam String type)
            //@RequestHeader("Authorization") String accessToken)
            {
        CategoryStatisticsResponse response = categoryStatisticsService.getCategoryStatistics(year, month, type);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/goal-per-total")
    public ApiResponse<GoalStatisticsResponse> getGoalStatistics(
            @RequestParam Long year,
            @RequestParam Long month,
            @RequestParam String type)
            //@RequestHeader("Authorization") String accessToken)
            {
        GoalStatisticsResponse response = categoryStatisticsService.getGoalStatistics(year, month, type);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/goal-per-category")
    public ApiResponse<CategoryGoalStatisticsResponse> getCategoryGoalStatistics(
            @RequestParam Long year,
            @RequestParam Long month,
            @RequestParam String type)
            //@RequestHeader("Authorization") String accessToken)
            {
        CategoryGoalStatisticsResponse response = categoryStatisticsService.getCategoryGoalStatistics(year, month, type);

        return ApiResponse.onSuccess(response);
    }

}
