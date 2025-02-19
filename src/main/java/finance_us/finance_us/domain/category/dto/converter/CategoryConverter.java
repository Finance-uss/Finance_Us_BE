package finance_us.finance_us.domain.category.dto.converter;

import finance_us.finance_us.domain.category.dto.CategoryRequestDto;
import finance_us.finance_us.domain.category.dto.CategoryResponseDto;
import finance_us.finance_us.domain.category.entity.MainCategory;
import finance_us.finance_us.domain.category.entity.SubCategory;
import finance_us.finance_us.domain.category.entity.status.CategoryType;
import finance_us.finance_us.domain.user.entity.User;

import java.util.ArrayList;

public class CategoryConverter
{
    public static CategoryResponseDto.MainResponseDto mainCategoryEntityToDto(MainCategory mainCategory)
    {
        return CategoryResponseDto.MainResponseDto.builder()
                .id(mainCategory.getId())
                .name(mainCategory.getMainName())
                .subCategories(new ArrayList<CategoryResponseDto.SubResponseDto>())
                .build();
    }

    public static CategoryResponseDto.SubResponseDto subCategoryEntityToDto(SubCategory subCategory)
    {
        return CategoryResponseDto.SubResponseDto.builder()
                .id(subCategory.getId())
                .name(subCategory.getSubName())
                .goal(subCategory.getGoal())
                .build();
    }

    public static MainCategory mainRequestDtoToEntity(CategoryRequestDto.MainRequestDto dto, Long userId)
    {
        return MainCategory.builder()
                .mainName(dto.getName())
                .categoryType(dto.getCategoryType())
                .user(User.builder().Id(userId).build())
                .build();
    }

    public static SubCategory subRequestDtoToEntity(CategoryRequestDto.SubRequestDto dto, Long userId)
    {
        return SubCategory.builder()
                .subName(dto.getName())
                .mainCategory(MainCategory.builder().id(dto.getMainId()).build())
                .goal(dto.getGoal())
                .user(User.builder().Id(userId).build())
                .build();
    }

    public static CategoryResponseDto.SubGoalResponseDto toSubGoalResponseDto(SubCategory subCategory)
    {
        return CategoryResponseDto.SubGoalResponseDto.builder()
                .name(subCategory.getSubName())
                .goal(subCategory.getGoal())
                .mainName(subCategory.getMainCategory().getMainName())
                .id(subCategory.getId())
                .build();

    }

}
