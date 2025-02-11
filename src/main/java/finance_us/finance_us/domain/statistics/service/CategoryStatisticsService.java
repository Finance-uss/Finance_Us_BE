package finance_us.finance_us.domain.statistics.service;

import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.category.entity.MainCategory;
import finance_us.finance_us.domain.category.entity.SubCategory;
import finance_us.finance_us.domain.category.repository.MainCategoryRepository;
import finance_us.finance_us.domain.category.repository.SubCategoryRepository;
import finance_us.finance_us.domain.statistics.dto.CategoryGoalStatisticsResponse;
import finance_us.finance_us.domain.statistics.dto.CategoryStatisticsResponse;
import finance_us.finance_us.domain.statistics.dto.GoalStatisticsResponse;
import finance_us.finance_us.domain.statistics.entity.CategoryStatistics;
import finance_us.finance_us.domain.statistics.entity.status.Type;
import finance_us.finance_us.domain.statistics.repository.CategoryStatisticsRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryStatisticsService {
    private final CategoryStatisticsRepository categoryStatisticsRepository;
    private final TokenProvider tokenProvider;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final UserRepository userRepository;

    public CategoryStatisticsResponse getCategoryStatistics(String token, Long year, Long month, String type){
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Type statisticsType = Type.valueOf(type.toUpperCase());

        // 🔹 통계를 조회할 때, 기존 메인 카테고리의 목표 금액을 업데이트
        updateCategoryGoalsForExistingStatistics(userId, year, month, statisticsType);

        List<CategoryStatistics> statistics = categoryStatisticsRepository.findByYearAndMonthAndTypeAndUserId(year, month, statisticsType,  userId);

        double totalSpent = statistics.stream().mapToDouble(CategoryStatistics::getTotalMoney).sum();

        List<CategoryStatisticsResponse.CategoryData> categoryData = statistics.stream()
                .map(stat -> new CategoryStatisticsResponse.CategoryData(
                        stat.getMainCategory().getMainName(),+
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

    public GoalStatisticsResponse getGoalStatistics(String token, Long year, Long month, String type){
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Type statisticsType = Type.valueOf(type.toUpperCase());

        // 🔹 통계를 조회할 때, 기존 메인 카테고리의 목표 금액을 업데이트
        updateCategoryGoalsForExistingStatistics(userId, year, month, statisticsType);

        List<CategoryStatistics> statistics = categoryStatisticsRepository.findByYearAndMonthAndTypeAndUserId(year, month, statisticsType, userId);

        double totalSpent = statistics.stream().mapToDouble(CategoryStatistics::getTotalMoney).sum();
        double totalGoal = statistics.stream().mapToDouble(CategoryStatistics::getTotalGoal).sum();

        Integer percentage = totalGoal == 0 ? 0 : (int)((totalSpent / totalGoal) * 100);

        return new GoalStatisticsResponse(year, month, type, (long) totalSpent, (long) totalGoal, percentage);
    }

    public CategoryGoalStatisticsResponse getCategoryGoalStatistics(String token, Long year, Long month, String type){
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Type statisticsType = Type.valueOf(type.toUpperCase());

        // 🔹 통계를 조회할 때, 기존 메인 카테고리의 목표 금액을 업데이트
        updateCategoryGoalsForExistingStatistics(userId, year, month, statisticsType);

        List<CategoryStatistics> statistics = categoryStatisticsRepository.findByYearAndMonthAndTypeAndUserId(year, month, statisticsType, userId);

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

    //가계부 생성 시 통계 업데이트
    @Transactional
    public void updateStatisticsOnCreate(String token, Account account){
        Long year = (long) account.getDate().getYear();
        Long month = (long) account.getDate().getMonthValue();
        Long userId = tokenProvider.extractUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        MainCategory mainCategory = account.getSubCategory().getMainCategory();

        //메인 카테고리 사용자 검증 로직
        if(!mainCategory.getUser().getId().equals(userId)){
            throw new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND);
        }

        Type type = Type.valueOf(account.getAccountType().name().toUpperCase());

        CategoryStatistics statistics = categoryStatisticsRepository
                .findByYearAndMonthAndTypeAndUserIdAndMainCategory(year, month, type, userId, mainCategory)
                .orElse(null);

        //카테고리별 통계 데이터가 없는 경우 생성
        if (statistics == null){
            statistics = CategoryStatistics.builder()
                    .year(year)
                    .month(month)
                    .type(type)
                    .totalMoney(account.getAmount())  // 초기값: 현재 가계부 금액
                    .totalGoal(0L)
                    .user(user)
                    .mainCategory(mainCategory)
                    .build();

            categoryStatisticsRepository.save(statistics);
        }
        //카테고리별 통계 데이터가 있는 경우 수정
        else {
            statistics.setTotalMoney(statistics.getTotalMoney() + account.getAmount());
        }
    }

    //가계부 수정 시 업데이트
    @Transactional
    public void updateStatisticsOnUpdate(String token, Account oldAccount, Account updatedAccount){
        Long year = (long) updatedAccount.getDate().getYear();
        Long month = (long) updatedAccount.getDate().getMonthValue();
        Long userId = tokenProvider.extractUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        MainCategory mainCategory = updatedAccount.getSubCategory().getMainCategory();

        //메인 카테고리 사용자 검증 로직
        if(!mainCategory.getUser().getId().equals(userId)){
            throw new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND);
        }

        // 기존 유형과 변경 후 유형이 다르면, 기존 통계에서 제거 후 새로운 통계에 추가
        Type oldType = Type.valueOf(oldAccount.getAccountType().name().toUpperCase());
        Type newType = Type.valueOf(updatedAccount.getAccountType().name().toUpperCase());

        if (!oldType.equals(newType)) {
            //기존 가계부의 유형에 맞는 통계 수정
            CategoryStatistics oldStatistics = categoryStatisticsRepository
                    .findByYearAndMonthAndTypeAndUserIdAndMainCategory(year, month, oldType, userId, mainCategory)
                    .orElse(null);

            if (oldStatistics != null) {
                oldStatistics.setTotalMoney(oldStatistics.getTotalMoney() - oldAccount.getAmount());
            }

            //새로운 가계부의 유형에 맞는 통계 수정
            CategoryStatistics newStatistics = categoryStatisticsRepository
                    .findByYearAndMonthAndTypeAndUserIdAndMainCategory(year, month, newType, userId, mainCategory)
                    .orElse(null);

            if (newStatistics == null) {
                // 새로운 유형의 통계가 없으면 생성
                newStatistics = CategoryStatistics.builder()
                        .year(year)
                        .month(month)
                        .type(newType)
                        .totalMoney(updatedAccount.getAmount())
                        .totalGoal(0L)
                        .user(user)
                        .mainCategory(mainCategory)
                        .build();
                categoryStatisticsRepository.save(newStatistics);
            } else {
                // 새로운 유형의 통계가 있으면 업데이트
                newStatistics.setTotalMoney(newStatistics.getTotalMoney() + updatedAccount.getAmount());
            }
        } else {
            //같은 유형이면 기존 로직 그대로 유지
            CategoryStatistics statistics = categoryStatisticsRepository
                    .findByYearAndMonthAndTypeAndUserIdAndMainCategory(year, month, newType, user.getId(), mainCategory)
                    .orElse(null);

            if (statistics != null) {
                statistics.setTotalMoney(statistics.getTotalMoney() - oldAccount.getAmount() + updatedAccount.getAmount());
            }
        }
    }

    //가계부 삭제 시 업데이트
    @Transactional
    public void  updateStatisticsOnDelete(String token, Account account){
        Long year = (long) account.getDate().getYear();
        Long month = (long) account.getDate().getMonthValue();
        Long userId = tokenProvider.extractUserIdFromToken(token);
        MainCategory mainCategory = account.getSubCategory().getMainCategory();

        //메인 카테고리 사용자 검증 로직
        if(!mainCategory.getUser().getId().equals(userId)){
            throw new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND);
        }

        Type type = Type.valueOf(account.getAccountType().name().toUpperCase());

        CategoryStatistics statistics = categoryStatisticsRepository
                .findByYearAndMonthAndTypeAndUserIdAndMainCategory(year, month, type, userId, mainCategory)
                .orElse(null);

        //삭제한 가계부 금액 차감
        if(statistics != null){
            statistics.setTotalMoney(statistics.getTotalMoney() - account.getAmount());
        }
    }


    //메인 카테고리 삭제 시 통계 데이터 삭제(아직 미사용)
    @Transactional
    public void deleteStatisticsOnCategoryDelete(Long userId, Long mainCategoryId) {
        categoryStatisticsRepository.deleteByUserIdAndMainCategoryId(userId, mainCategoryId);
    }

    @Transactional
    public void updateCategoryGoalsForExistingStatistics(Long userId, Long year, Long month, Type type) {
        // 🔹 해당 월의 통계가 존재하는 메인 카테고리를 조회
        List<CategoryStatistics> existingStatistics = categoryStatisticsRepository.findByYearAndMonthAndTypeAndUserId(year, month, type, userId);

        for (CategoryStatistics stat : existingStatistics) {
            Long mainCategoryId = stat.getMainCategory().getId();

            // 🔹 해당 메인 카테고리에 속하는 서브 카테고리의 목표 금액 합산
            Long totalGoal = subCategoryRepository.findByUserIdAndMainCategoryId(userId, mainCategoryId)
                    .stream()
                    .filter(subCategory -> subCategory.getGoal() != null)
                    .mapToLong(SubCategory::getGoal)
                    .sum();

            //목표 금액 업데이트
            stat.setTotalGoal(totalGoal);
        }

        //변경된 통계 정보를 저장
        categoryStatisticsRepository.saveAll(existingStatistics);
    }

}
