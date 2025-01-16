package finance_us.finance_us.domain.category.dto;

import lombok.*;

import java.util.List;

public class AssetResponseDto
{
    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public static class MainResponseDto
    {
        long id;
        String name;
        List<AssetResponseDto.SubResponseDto> subAssets;
    }

    @Getter @Setter @RequiredArgsConstructor @AllArgsConstructor @ToString @Builder
    public static class SubResponseDto
    {
        long id;
        String name;
    }

}
