package finance_us.finance_us.domain.category.dto;

import finance_us.finance_us.domain.category.entity.status.CategoryType;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

public class CategoryRequestDto
{

    @Getter
    @Setter @AllArgsConstructor @RequiredArgsConstructor
    @ToString @Builder
    public static class UpdateRequestDto
    {
        Long id;
        String name;
    }

    @Getter
    @Setter @AllArgsConstructor @RequiredArgsConstructor
    @ToString @Builder
    public static class UpdateGoalDto
    {
        CategoryType type;
        List<GoalRequestDto> subGoals;
    }

    @Getter
    @Setter @AllArgsConstructor @RequiredArgsConstructor
    @ToString @Builder
    public static class MainRequestDto
    {
        String name;
        CategoryType categoryType;
    }

    @Getter
    @Setter @AllArgsConstructor @RequiredArgsConstructor
    @ToString @Builder
    public static class SubRequestDto
    {
        String name;
        int goal;

        Long mainId;
    }

    @Getter
    @Setter @AllArgsConstructor @RequiredArgsConstructor
    @ToString @Builder
    public static class GoalRequestDto
    {
        int goal;
        Long id;
    }



}
