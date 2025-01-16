package finance_us.finance_us.domain.category.service;

import finance_us.finance_us.domain.category.dto.AssetRequestDto;
import finance_us.finance_us.domain.category.dto.AssetResponseDto;
import finance_us.finance_us.domain.category.dto.CategoryRequestDto;
import finance_us.finance_us.domain.category.dto.CategoryResponseDto;
import finance_us.finance_us.domain.category.dto.converter.AssetConverter;
import finance_us.finance_us.domain.category.dto.converter.CategoryConverter;
import finance_us.finance_us.domain.category.entity.MainAsset;
import finance_us.finance_us.domain.category.entity.SubAsset;
import finance_us.finance_us.domain.category.entity.status.CategoryType;
import finance_us.finance_us.domain.category.repository.MainAssetRepository;
import finance_us.finance_us.domain.category.repository.SubAssetRepository;
import finance_us.finance_us.domain.category.repository.MainCategoryRepository;
import finance_us.finance_us.domain.category.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class CategoryService
{
    final private MainCategoryRepository mainCategoryRepository;
    final private SubCategoryRepository subCategoryRepository;
    final private MainAssetRepository mainAssetRepository;
    final private SubAssetRepository subAssetRepository;

    // userId : 대상으로 할 유저
    // type   : 지출 / 수입
    // 유저의 카테고리를 목록화하여 반환
    public List<CategoryResponseDto.MainResponseDto> getCategoryList(Long userId, CategoryType type)
    {
        // 메인 카테고리 정보를 불러온다.
        var list = mainCategoryRepository.findByUserIdAndCategoryType(userId, type);

        // 반환할 메인카테고리 배열
        var array = new ArrayList<CategoryResponseDto.MainResponseDto>();
        for(var item : list)
        {
            // 각 아이템을 메인 카테고리 Dto로 변환.
            var main = CategoryConverter.mainCategoryEntityToDto(item);

            // !! updateCategory 함수에서 호출 시 fetch가 작동하지 않아 따로 호출함. !!
            var subCategories = subCategoryRepository.findByUserIdAndMainCategoryId(userId, main.getId());

            for(var subItem : subCategories)
            {
                // 메인 카테고리 Dto내에 sub카테고리를 삽입
                var sub = CategoryConverter.subCategoryEntityToDto(subItem);
                main.getSubCategories().add(sub);
            }

            // 반환할 배열에 메인카테고리 삽입.
            array.add(main);
        }

        return array;
    }

    // 유저의 모든 카테고리를 삭제 후, 요청 받은 목록을 기반으로 다시 카테고리 생성
    public List<CategoryResponseDto.MainResponseDto> updateCategory(Long userId, CategoryType type, List<CategoryRequestDto.MainRequestDto> categories)
    {
        List<Long> deleteIds = mainCategoryRepository.findByUserIdAndCategoryType(userId, type)
                                                        .stream().map(m -> m.getId())
                                                        .collect(Collectors.toList());
        mainCategoryRepository.deleteAllById(deleteIds);

        for(var mainItem : categories)
        {
            var main = mainCategoryRepository.save(CategoryConverter.mainRequestDtoToEntity(userId, type, mainItem));

            for(var subItem : mainItem.getSubCategories())
            {
                subCategoryRepository.save(CategoryConverter.subRequestDtoToEntity(userId, main.getId(), subItem));
            }
        }

        return getCategoryList(userId, type);
    }


    // 유저의 자산을 목록화하여 반환
    public List<AssetResponseDto.MainResponseDto> getAssetList(Long userId)
    {
        var list = mainAssetRepository.findByUserId(userId);

        // 메인 DTO를 리스트로 만들어줌. (서브 자산은 CONVERTER에서 처리)
        var mainDtoArray = list.stream().map(AssetConverter::mainAssetEntityToDto)
                            .toList();

        return mainDtoArray;
    }

    public List<AssetResponseDto.MainResponseDto> updateAsset(Long userId, List<AssetRequestDto.MainRequestDto> dtoList)
    {
        var deleteList = mainAssetRepository.findByUserId(userId)
                .stream().map(MainAsset::getId).toList();
        mainAssetRepository.deleteAllById(deleteList);

        // SubAsset 로직은 Converter에서 처리
        // dto를 entity로 바꾸어 저장
        var mainList = dtoList.stream()
                        .map(item -> AssetConverter.mainAssetRequestDtoToEntity(userId, item))
                        .toList();

        var mainEntityList = mainAssetRepository.saveAll(mainList);

        // 서브 엔티티 들의 main_asset_id를 추가해주는 과정
        var subEntityList = new ArrayList<SubAsset>();
        for(var m : mainEntityList)
        {
            subEntityList.addAll(m.getSubAssets().stream().peek((s) -> s.setMainAsset(m)).toList());
        }
        subAssetRepository.saveAll(subEntityList);

        return getAssetList(userId);
    }



}
