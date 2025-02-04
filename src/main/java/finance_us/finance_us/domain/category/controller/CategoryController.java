package finance_us.finance_us.domain.category.controller;

import finance_us.finance_us.domain.category.dto.AssetRequestDto;
import finance_us.finance_us.domain.category.dto.CategoryRequestDto;
import finance_us.finance_us.domain.category.entity.status.CategoryType;
import finance_us.finance_us.domain.category.service.CategoryService;
import finance_us.finance_us.global.ApiResponse;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.global.file.S3FileService;
import finance_us.finance_us.security.JwtAuthenticationFilter;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor @Slf4j
public class CategoryController
{
    private final CategoryService categoryService;
    private final S3FileService s3FileService;
    private final TokenProvider tokenProvider;

    @GetMapping("/api/mypage/category")
    public ApiResponse<?> getCategory(@RequestHeader("Authorization") String token, String type) {
        // ENUM 기준을 틀렸을 경우
        CategoryType categoryType;
        try {
            categoryType = CategoryType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.CATEGORY_TYPE_ERROR);
        }

        Long userId = tokenProvider.extractUserIdFromToken(token);

        return ApiResponse.onSuccess(categoryService.getCategoryList(userId, categoryType));
    }

    @PatchMapping("/api/mypage/category")
    public ApiResponse<?> updateCategory(@RequestHeader("Authorization") String token, @RequestBody CategoryRequestDto.UpdateRequestDto dto)
    {
        // ENUM 기준을 틀렸을 경우
        CategoryType categoryType;
        try {
            categoryType = CategoryType.valueOf(dto.getType().toUpperCase());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.CATEGORY_TYPE_ERROR);
        }

        Long userId = tokenProvider.extractUserIdFromToken(token);

        return ApiResponse.onSuccess(categoryService.updateCategory(userId, categoryType, dto.getMainCategories()));
    }

    @GetMapping("/api/mypage/asset")
    public ApiResponse<?> getAssetList(@RequestHeader("Authorization") String token)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        return ApiResponse.onSuccess(categoryService.getAssetList(userId));
    }

    @PatchMapping("/api/mypage/asset")
    public ApiResponse<?> updateAsset(@RequestHeader("Authorization") String token, @RequestBody List<AssetRequestDto.MainRequestDto> assetList)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        return ApiResponse.onSuccess(categoryService.updateAsset(userId, assetList));
    }
  
      @PatchMapping("/api/mypage/goal-asset")
    public ApiResponse<?> updateCategoryGoal(@RequestHeader("Authorization") String token, @RequestBody CategoryRequestDto.UpdateGoalDto dto)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        // ENUM 기준을 틀렸을 경우
        CategoryType categoryType;
        try {
            categoryType = CategoryType.valueOf(dto.getType().toUpperCase());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.CATEGORY_TYPE_ERROR);
        }

        return ApiResponse.onSuccess(categoryService.updateCategoryGoal(userId, categoryType, dto.getSubCategories()));

    }

    @GetMapping("/api/mypage/goal-asset")
    public ApiResponse<?> getGoalAsset(@RequestHeader("Authorization") String token, String type)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        CategoryType categoryType;
        try {
            categoryType = CategoryType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.CATEGORY_TYPE_ERROR);
        }
        return ApiResponse.onSuccess(categoryService.getGoalList(userId, categoryType));
    }

}

