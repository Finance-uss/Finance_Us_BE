package finance_us.finance_us.domain.category.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import finance_us.finance_us.domain.category.dto.CategoryRequestDto;
import finance_us.finance_us.domain.category.dto.CategoryResponseDto;
import finance_us.finance_us.domain.category.dto.converter.CategoryConverter;
import finance_us.finance_us.domain.category.entity.category.MainCategory;
import finance_us.finance_us.domain.category.entity.category.status.CategoryType;
import finance_us.finance_us.domain.category.repository.asset.MainAssetRepository;
import finance_us.finance_us.domain.category.repository.asset.SubAssetRepository;
import finance_us.finance_us.domain.category.repository.category.MainCategoryRepository;
import finance_us.finance_us.domain.category.repository.category.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service @RequiredArgsConstructor @Slf4j
public class CategoryService
{
    final private MainCategoryRepository mainCategoryRepository;
    final private SubCategoryRepository subCategoryRepository;
    final private MainAssetRepository mainAssetRepository;
    final private SubAssetRepository subAssetRepository;

    // userId : 대상으로 할 유저
    // type   : 지출 / 수입
    public List<CategoryResponseDto.MainResponseDto> getCategory(Long userId, CategoryType type)
    {
        // 메인 카테고리 정보를 불러온다.
        var list = mainCategoryRepository.findByUserIdAndCategoryType(userId, type);

        // 반환할 메인카테고리 배열
        var array = new ArrayList<CategoryResponseDto.MainResponseDto>();
        for(var item : list)
        {
            // 각 아이템을 메인 카테고리 Dto로 변환.
            var main = CategoryConverter.mainCategoryToDto(item);
            for(var subItem : item.getSubCategories())
            {
                // 메인카테고리Dto내에 sub카테고리를 삽입
                var sub = CategoryConverter.subCategoryToDto(subItem);
                main.getSubCategories().add(sub);
            }

            // 반환할 배열에 메인카테고리 삽입.
            array.add(main);
        }

        return array;
    }

    public List<CategoryResponseDto.MainResponseDto> updateCategory(Long userId, CategoryType type, List<CategoryRequestDto.MainRequestDto> categories)
    {
        mainCategoryRepository.deleteByUserIdAndCategoryType(userId, type);

        for(var mainItem : categories)
        {
            mainCategoryRepository.save(CategoryConverter.mainRequestToEntity(userId, type, mainItem));
            for(var subItem : mainItem.getSubCategories())
            {
                subCategoryRepository.save(CategoryConverter.subRequestToEntity(userId, subItem));
            }
        }

        return getCategory(userId, type);
    }

}
