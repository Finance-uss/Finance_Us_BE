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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor @Slf4j
@Tag(name="Category/Asset API",  description = "카테고리/자산 관련 API")
public class CategoryController
{
    private final CategoryService categoryService;
    private final S3FileService s3FileService;
    private final TokenProvider tokenProvider;

    @GetMapping("/api/mypage/category")
    @Operation(summary = "카테고리 조회 API", description = "타입을 바탕으로 카테고리 리스트를 받습니다")
    public ApiResponse<?> getCategory(@RequestHeader("Authorization") String token, String type) {
        // ENUM 기준을 틀렸을 경우
        CategoryType categoryType;
        try {
            categoryType = CategoryType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.CATEGORY_TYPE_ERROR);
        }

        Long userId = tokenProvider.extractUserIdFromToken(token);
        log.info(categoryType.name());
        return ApiResponse.onSuccess(categoryService.getCategoryList(userId, categoryType));
    }

    @PostMapping("/api/mypage/category/main")
    @Operation(summary = "메인 카테고리 생성 API", description = "메인 카테고리를 생성합니다.")
    public ApiResponse<?> createMainCategory(@RequestHeader("Authorization") String token, @RequestBody CategoryRequestDto.MainRequestDto categoryRequestDto)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        return ApiResponse.onSuccess(categoryService.createMainCategory(categoryRequestDto, userId));
    }

    @PostMapping("/api/mypage/category/sub")
    @Operation(summary = "서브 카테고리 생성 API", description = "서브 카테고리를 생성합니다.")
    public ApiResponse<?> createSubCategory(@RequestHeader("Authorization") String token, @RequestBody CategoryRequestDto.SubRequestDto dto)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        return ApiResponse.onSuccess(categoryService.createSubCategory(dto, userId));
    }

    @PatchMapping("/api/mypage/category/main")
    @Operation(summary = "메인 카테고리 수정 API", description = "메인 카테고리를 수정합니다.")
    public ApiResponse<?> updateMainCategory(@RequestHeader("Authorization") String token, @RequestBody CategoryRequestDto.UpdateRequestDto updateRequestDto)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        categoryService.updateMainCategory(updateRequestDto.getId(), updateRequestDto.getName());
        return ApiResponse.onSuccess("updated : main_category");
    }

    @PatchMapping("/api/mypage/category/sub")
    @Operation(summary = "서브 카테고리 수정 API", description = "서브 카테고리를 수정합니다.")
    public ApiResponse<?> updateSubCategory(@RequestHeader("Authorization") String token, @RequestBody CategoryRequestDto.UpdateRequestDto dto)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        categoryService.updateSubCategory(dto.getId(), dto.getName());
        return ApiResponse.onSuccess("updated : sub_category");
    }

    @DeleteMapping("/api/mypage/category/main")
    @Operation(summary = "메인 카테고리 삭제 API", description = "메인 카테고리를 삭제합니다.")
    public ApiResponse<?> deleteMainCategory(@RequestHeader("Authorization") String token, Long mainId)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        categoryService.deleteMainCategory(mainId);
        return ApiResponse.onSuccess("deleted : main_category");
    }

    @DeleteMapping("/api/mypage/category/sub")
    @Operation(summary = "서브 카테고리 삭제 API", description = "서브 카테고리를 삭제합니다.")
    public ApiResponse<?> deleteSubCategory(@RequestHeader("Authorization") String token, Long subId)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        categoryService.deleteSubCategory(subId);
        return ApiResponse.onSuccess("deleted : sub_category");
    }

    @GetMapping("/api/mypage/asset")
    @Operation(summary = "자산 조회 API", description = "자산을 조회합니다.")
    public ApiResponse<?> getAssetList(@RequestHeader("Authorization") String token)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        return ApiResponse.onSuccess(categoryService.getAssetList(userId));
    }


    @PostMapping("/api/mypage/asset/main")
    @Operation(summary = "메인 자산 생성 API", description = "메인 자산을 생성합니다.")
    public ApiResponse<?> createMainAsset(@RequestHeader("Authorization") String token, String mainName)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        return ApiResponse.onSuccess(categoryService.createMainAsset(mainName, userId));
    }

    @PostMapping("/api/mypage/asset/sub")
    @Operation(summary = "서브 자산 생성 API", description = "서브 자산을 생성합니다.")
    public ApiResponse<?> createSubAsset(@RequestHeader("Authorization") String token, String subName, Long mainId)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        return ApiResponse.onSuccess(categoryService.createSubAsset(subName, mainId, userId));
    }

    @PatchMapping("/api/mypage/asset/main")
    @Operation(summary = "메인 자산 수정 API", description = "메인 자산을 수정합니다.")
    public ApiResponse<?> updateMainAsset(@RequestHeader("Authorization") String token, String subName, Long mainId)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        categoryService.updateMainAsset(mainId, subName);
        return ApiResponse.onSuccess("updated : main_asset");
    }

    @PatchMapping("/api/mypage/asset/sub")
    @Operation(summary = "서브 자산 수정 API", description = "서브 자산을 수정합니다.")
    public ApiResponse<?> updateSubAsset(@RequestHeader("Authorization") String token, @RequestBody CategoryRequestDto.UpdateRequestDto dto)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        categoryService.updateSubCategory(dto.getId(), dto.getName());
        return ApiResponse.onSuccess("updated : sub_category");
    }

    @DeleteMapping("/api/mypage/asset/main")
    @Operation(summary = "메인 자산 삭제 API", description = "메인 자산을 삭제합니다.")
    public ApiResponse<?> deleteMainAsset(@RequestHeader("Authorization") String token, Long mainId)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        categoryService.deleteMainAsset(mainId);
        return ApiResponse.onSuccess("deleted : main_asset");
    }

    @DeleteMapping("/api/mypage/asset/sub")
    @Operation(summary = "서브 자산을 삭제 API", description = "서브 자산을 삭제합니다.")
    public ApiResponse<?> deleteSubAsset(@RequestHeader("Authorization") String token, Long subId)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        categoryService.deleteSubAsset(subId);

        return ApiResponse.onSuccess("deleted : sub_asset");
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

    @PatchMapping("/api/mypage/goal-asset")
    public ApiResponse<?> getGoalAsset(@RequestHeader("Authorization") String token, CategoryRequestDto.UpdateGoalDto dto)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        return ApiResponse.onSuccess(categoryService.updateCategoryGoal(userId, dto.getType(), dto.getSubGoals()));
    }

    @PostMapping("/api/mypage/init-test")
    public ApiResponse<?> initTest(@RequestHeader("Authorization") String token)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        categoryService.initializeCategoryExpense(userId);
        categoryService.initializeCategoryIncome(userId);
        categoryService.initializeAsset(userId);

        return ApiResponse.onSuccess("init success");
    }


}

