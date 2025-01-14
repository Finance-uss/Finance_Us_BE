package finance_us.finance_us.domain.category.dto;

import lombok.*;

import java.util.List;

public class CategoryResponseDto
{
    @Getter @Setter @RequiredArgsConstructor @AllArgsConstructor @ToString @Builder
    public static class MainResponseDto
    {
        long id;
        String name;
        List<SubResponseDto> subCategories;
    }

    @Getter @Setter @RequiredArgsConstructor @AllArgsConstructor @ToString @Builder
    public static class SubResponseDto
    {
        long id;
        String name;
        int goal;
    }

}
