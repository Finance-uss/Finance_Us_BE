package finance_us.finance_us.domain.category.dto;

import lombok.*;

import java.util.List;

public class AssetRequestDto
{
    @Getter
    @Setter @AllArgsConstructor @RequiredArgsConstructor
    @ToString @Builder
    public static class MainRequestDto
    {
        String name;
        List<SubRequestDto> subAssets;
    }

    @Getter
    @Setter @AllArgsConstructor @RequiredArgsConstructor
    @ToString @Builder
    public static class SubRequestDto
    {
        String name;
    }

}
