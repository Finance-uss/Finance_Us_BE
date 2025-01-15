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

    public static MainCategory mainRequestDtoToEntity(Long userId, CategoryType type, CategoryRequestDto.MainRequestDto dto)
    {
        return MainCategory.builder()
                .mainName(dto.getName())
                .categoryType(type)
                .user(User.builder().Id(userId).build())
                .build();
    }

    public static SubCategory subRequestDtoToEntity(Long userId, Long mainId, CategoryRequestDto.SubRequestDto dto)
    {
        return SubCategory.builder()
                .subName(dto.getName())
                .mainCategory(MainCategory.builder().id(mainId).build())
                .goal(dto.getGoal())
                .user(User.builder().Id(userId).build())
                .build();
    }

}
