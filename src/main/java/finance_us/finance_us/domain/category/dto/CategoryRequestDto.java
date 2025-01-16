package finance_us.finance_us.domain.category.dto;

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
        String type;
        List<MainRequestDto> mainCategories;
    }



    @Getter
    @Setter @AllArgsConstructor @RequiredArgsConstructor
    @ToString @Builder
    public static class MainRequestDto
    {
        Long id;
        String name;
        List<SubRequestDto> subCategories;
    }

    @Getter
    @Setter @AllArgsConstructor @RequiredArgsConstructor
    @ToString @Builder
    public static class SubRequestDto
    {
        Long id;
        String name;
        int goal;

        Long mainId;
    }
}
