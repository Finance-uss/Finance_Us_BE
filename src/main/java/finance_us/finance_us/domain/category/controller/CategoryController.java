package finance_us.finance_us.domain.category.controller;

import finance_us.finance_us.domain.category.dto.CategoryRequestDto;
import finance_us.finance_us.domain.category.dto.CategoryResponseDto;
import finance_us.finance_us.domain.category.entity.category.status.CategoryType;
import finance_us.finance_us.domain.category.service.CategoryService;
import finance_us.finance_us.global.ApiResponse;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor @Slf4j
public class CategoryController
{
    private final CategoryService categoryService;

    @GetMapping("/api/mypage/category")
    public ApiResponse<?> getCategory(String type) {
        // ENUM 기준을 틀렸을 경우
        CategoryType categoryType;
        try {
            categoryType = CategoryType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.CATEGORY_TYPE_ERROR);
        }

        Long userId = 1L; // 유저로직 추가되면 수정

        return ApiResponse.onSuccess(categoryService.getCategory(userId, categoryType));
    }

    @PostMapping("/api/mypage/category")
    public ApiResponse<?> updateCategory(String type, List<CategoryRequestDto.MainRequestDto> mainCategories)
    {
        // ENUM 기준을 틀렸을 경우
        CategoryType categoryType;
        try {
            categoryType = CategoryType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.CATEGORY_TYPE_ERROR);
        }

        log.info(mainCategories.toString());

        Long userId = 1L; // 유저로직 추가되면 수정

        return ApiResponse.onSuccess(categoryService.updateCategory(userId, categoryType, mainCategories));
    }

}

