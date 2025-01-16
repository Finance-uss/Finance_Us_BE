package finance_us.finance_us.domain.statistics.service;

import finance_us.finance_us.domain.statistics.dto.CategoryGoalStatisticsResponse;
import finance_us.finance_us.domain.statistics.dto.CategoryStatisticsResponse;
import finance_us.finance_us.domain.statistics.dto.GoalStatisticsResponse;
import finance_us.finance_us.domain.statistics.entity.CategoryStatistics;
import finance_us.finance_us.domain.statistics.entity.status.Type;
import finance_us.finance_us.domain.statistics.repository.CategoryStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryStatisticsService {
    private final CategoryStatisticsRepository categoryStatisticsRepository;

    public CategoryStatisticsResponse getCategoryStatistics(Long year, Long month, String type){
        Type statisticsType = Type.valueOf(type.toUpperCase());

        List<CategoryStatistics> statistics = categoryStatisticsRepository.findByYearAndMonthAndType(year, month, statisticsType);

        double totalSpent = statistics.stream().mapToDouble(CategoryStatistics::getTotalMoney).sum();

        List<CategoryStatisticsResponse.CategoryData> categoryData = statistics.stream()
                .map(stat -> new CategoryStatisticsResponse.CategoryData(
                        stat.getMainCategory().getMainName(),
                        stat.getTotalMoney(),
                        (int)(((double) stat.getTotalMoney() / totalSpent) * 100)
                ))
                .collect(Collectors.toList());

        int totalPercentage = categoryData.stream()
                .mapToInt(CategoryStatisticsResponse.CategoryData::getPercentage)
                .sum();

        int difference = 100 - totalPercentage;
        if (difference != 0) {
            CategoryStatisticsResponse.CategoryData largestCategory = categoryData.stream()
                    .max((c1, c2) -> Long.compare(c1.getTotalSpent(), c2.getTotalSpent()))
                    .orElse(null);

            if (largestCategory != null) {
                largestCategory.setPercentage(largestCategory.getPercentage() + difference);
            }
        }

        return new CategoryStatisticsResponse(year, month, type, categoryData);

    }

    public GoalStatisticsResponse getGoalStatistics(Long year, Long month, String type){
        Type statisticsType = Type.valueOf(type.toUpperCase());

        List<CategoryStatistics> statistics = categoryStatisticsRepository.findByYearAndMonthAndType(year, month, statisticsType);

        double totalSpent = statistics.stream().mapToDouble(CategoryStatistics::getTotalMoney).sum();
        double totalGoal = statistics.stream().mapToDouble(CategoryStatistics::getTotalGoal).sum();

        Integer percentage = totalGoal == 0 ? 0 : (int)((totalSpent / totalGoal) * 100);

        return new GoalStatisticsResponse(year, month, type, (long) totalSpent, (long) totalGoal, percentage);
    }

    public CategoryGoalStatisticsResponse getCategoryGoalStatistics(Long year, Long month, String type){
        Type statisticsType = Type.valueOf(type.toUpperCase());

        List<CategoryStatistics> statistics = categoryStatisticsRepository.findByYearAndMonthAndType(year, month, statisticsType);

        List<CategoryGoalStatisticsResponse.CategoryGoalData> categoryGoalData = statistics.stream()
                .map(stat -> new CategoryGoalStatisticsResponse.CategoryGoalData(
                        stat.getMainCategory().getMainName(),
                        stat.getTotalMoney(),
                        stat.getTotalGoal(),
                        (int)((stat.getTotalMoney() / (double) stat.getTotalGoal()) * 100)
                ))
                .collect(Collectors.toList());

        return new CategoryGoalStatisticsResponse(year, month, type, categoryGoalData);
    }



}
