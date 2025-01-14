package finance_us.finance_us.domain.category.dto;

import lombok.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public class CategoryRequestDto
{
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
    }
}
