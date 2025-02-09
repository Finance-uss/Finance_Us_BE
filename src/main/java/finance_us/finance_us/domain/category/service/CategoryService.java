package finance_us.finance_us.domain.category.service;

import finance_us.finance_us.domain.category.dto.AssetRequestDto;
import finance_us.finance_us.domain.category.dto.AssetResponseDto;
import finance_us.finance_us.domain.category.dto.CategoryRequestDto;
import finance_us.finance_us.domain.category.dto.CategoryResponseDto;
import finance_us.finance_us.domain.category.dto.converter.AssetConverter;
import finance_us.finance_us.domain.category.dto.converter.CategoryConverter;
import finance_us.finance_us.domain.category.entity.MainAsset;
import finance_us.finance_us.domain.category.entity.MainCategory;
import finance_us.finance_us.domain.category.entity.SubAsset;
import finance_us.finance_us.domain.category.entity.SubCategory;
import finance_us.finance_us.domain.category.entity.status.CategoryType;
import finance_us.finance_us.domain.category.repository.MainAssetRepository;
import finance_us.finance_us.domain.category.repository.SubAssetRepository;
import finance_us.finance_us.domain.category.repository.MainCategoryRepository;
import finance_us.finance_us.domain.category.repository.SubCategoryRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
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

    // 카테고리를 생성하는 부분
    public CategoryResponseDto.MainResponseDto createMainCategory(CategoryRequestDto.MainRequestDto dto, Long userId)
    {
        var category = CategoryConverter.mainRequestDtoToEntity(dto, userId);

        var c = mainCategoryRepository.save(category);
        return CategoryConverter.mainCategoryEntityToDto(c);
    }

    public CategoryResponseDto.SubResponseDto createSubCategory(CategoryRequestDto.SubRequestDto dto, Long userId) {

        var category = CategoryConverter.subRequestDtoToEntity(dto, userId);
        var c = subCategoryRepository.save(category);

        return CategoryConverter.subCategoryEntityToDto(c);
    }

    // 카테고리를 수정하는 부분
    public void updateMainCategory(Long mainId, String mainName)
    {
        var mainCategory = mainCategoryRepository.findById(mainId).orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));
        mainCategory.setMainName(mainName);
        mainCategoryRepository.save(mainCategory);

    }

    public void updateSubCategory(Long subId, String subName)
    {
        var subCategory = subCategoryRepository.findById(subId).orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));
        subCategory.setSubName(subName);
        subCategoryRepository.save(subCategory);
    }

    // 카테고리를 삭제하는 부분
    public Long deleteMainCategory(Long mainId)
    {
        mainCategoryRepository.deleteById(mainId);
        return mainId;
    }

    public Long deleteSubCategory(Long subId)
    {
        subCategoryRepository.deleteById(subId);
        return subId;
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
    // 자산을 생성하는 부분
    public AssetResponseDto.MainResponseDto createMainAsset(String mainName, Long userId)
    {
        var mainAsset = MainAsset.builder()
                    .mainName(mainName)
                    .user(User.builder().Id(userId).build())
                    .build();

        var c = mainAssetRepository.save(mainAsset);
        return AssetConverter.mainAssetEntityToDto(c);
    }

    public AssetResponseDto.SubResponseDto createSubAsset(String subName, Long mainId, Long userId) {

        var subAsset = SubAsset.builder()
                .subName(subName)
                .mainAsset(MainAsset.builder().id(mainId).build())
                .user(User.builder().Id(userId).build())
                .build();

        var c = subAssetRepository.save(subAsset);
        return AssetConverter.subAssetEntityToDto(c);
    }

    // 자산을 수정하는 부분
    public void updateMainAsset(Long mainId, String mainName)
    {
        var mainAsset = mainAssetRepository.findById(mainId).orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));
        mainAsset.setMainName(mainName);
        mainAssetRepository.save(mainAsset);

    }

    public void updateSubAsset(Long subId, String subName)
    {
        var subAsset = subAssetRepository.findById(subId).orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));
        subAsset.setSubName(subName);
        subAssetRepository.save(subAsset);
    }

    // 자산을 삭제하는 부분
    public Long deleteMainAsset(Long mainId)
    {
        mainAssetRepository.deleteById(mainId);
        return mainId;
    }

    public Long deleteSubAsset(Long subId)
    {
        mainAssetRepository.deleteById(subId);
        return subId;
    }

    // 이번 달 목표 금액 / 카테고리별 목표 금액 조회
    public CategoryResponseDto.GoalResponseDto getGoalList(Long userId, CategoryType type)
    {
        var categoryList = subCategoryRepository.findByGoal(userId, type);

        var monthlyGoal = categoryList.stream()
                        .mapToInt(SubCategory::getGoal)
                        .sum();
        var subCategoryList = categoryList.stream()
                .map(CategoryConverter::subCategoryEntityToDto)
                .toList();

        return CategoryResponseDto.GoalResponseDto.builder()
                .monthlyGoal(monthlyGoal)
                .subCategories(subCategoryList)
                .build();
    }



    // 목표 금액 업데이트 로직
    public CategoryResponseDto.GoalResponseDto updateCategoryGoal(Long userId, CategoryType type, List<CategoryRequestDto.GoalRequestDto> subGoals)
    {
        // GOAL 값 초기화( 꼴값 초기화 )
        var categoryList = subCategoryRepository.findByType(userId, type);
        categoryList = categoryList.stream()
                    .peek(s -> s.setGoal(-1))
                    .toList();

        var updatedList = new ArrayList<SubCategory>();
        // GOAL 다시 박아주기
        for(var s : subGoals)
        {
            var entity = categoryList.stream()
                        .filter(e -> e.getId().equals(s.getId()))
                        .findFirst().orElseThrow();

            entity.setGoal(s.getGoal());
            updatedList.add(entity);
        }

        // 수정된 요소 반영
        subCategoryRepository.saveAll(updatedList);

        return getGoalList(userId, type);
    }



}
