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
import finance_us.finance_us.domain.statistics.service.CategoryStatisticsService;
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
    final private CategoryStatisticsService categoryStatisticsService;

    // userId : 대상으로 할 유저
    // type   : 지출 / 수입
    // 유저의 카테고리를 목록화하여 반환
    public List<CategoryResponseDto.MainResponseDto> getCategoryList(Long userId, CategoryType type)
    {
        // 메인 카테고리 정보를 불러온다.
        var list = mainCategoryRepository.findByUserIdAndCategoryType(userId, type.name());
        log.info(list.toString());

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
    public Long deleteMainCategory(Long mainId, Long userId)
    {
        //메인 카테고리 존재 확인 및 userId 일치 검증
        MainCategory mainCategory = mainCategoryRepository.findById(mainId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND));

        if(!mainCategory.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND);
        }

        //메인 카테고리와 연관된 서브 카테고리의 존재 여부 확인 (존재한다면 삭제 불가)
        List<SubCategory> subCategories = subCategoryRepository.findByUserIdAndMainCategoryId(userId, mainId);
        if (!subCategories.isEmpty()) {
            throw new GeneralException(ErrorStatus.MAINCATEGORY_HAS_SUBCATEGORY);
        }

        // 🔹 메인 카테고리에 연결된 통계 삭제
        categoryStatisticsService.deleteStatisticsOnCategoryDelete(userId, mainId);

        mainCategoryRepository.deleteById(mainId);
        return mainId;
    }

    public Long deleteSubCategory(Long subId)
    {
        // 가계부가 연결된 서브 카테고리 리스트를 뽑아내준다. (가게부가 있으면 삭제할 수 없음)
        var list = subCategoryRepository.linkedAccount(subId);
        if(!list.isEmpty()) throw new GeneralException(ErrorStatus.SUBCATEGORY_HAS_ACCOUNT);

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
                .map(CategoryConverter::toSubGoalResponseDto)
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

    // 유저가 회원가입을 했을 때 자산과 카테고리를 초기화 시켜주는 부분
    public void initializeCategoryExpense(Long userId)
    {
        User user = User.builder().Id(userId).build();
        // 식비
        var main1 = mainCategoryRepository.save(MainCategory.builder()
                .mainName("식비")
                .categoryType(CategoryType.EXPENSE)
                .user(user)
                .build());
        // 교통
        var main2 = mainCategoryRepository.save(MainCategory.builder()
                .mainName("교통")
                .categoryType(CategoryType.EXPENSE)
                .user(user)
                .build());
        // 교통
        var main3 = mainCategoryRepository.save(MainCategory.builder()
                .mainName("여가/취미")
                .categoryType(CategoryType.EXPENSE)
                .user(user)
                .build());

        var subList = new ArrayList<SubCategory>();
        // 식비
        subList.add(SubCategory.builder()
                .subName("외식")
                .goal(0)
                .user(user)
                .mainCategory(main1)
                .build());
        subList.add(SubCategory.builder()
                .subName("배달")
                .goal(0)
                .user(user)
                .mainCategory(main1)
                .build());
        subList.add(SubCategory.builder()
                .subName("식재료")
                .goal(0)
                .user(user)
                .mainCategory(main1)
                .build());

        // 교통
        subList.add(SubCategory.builder()
                .subName("대중교통")
                .goal(0)
                .user(user)
                .mainCategory(main2)
                .build());
        subList.add(SubCategory.builder()
                .subName("택시")
                .goal(0)
                .user(user)
                .mainCategory(main2)
                .build());
        subList.add(SubCategory.builder()
                .subName("주유")
                .goal(0)
                .user(user)
                .mainCategory(main2)
                .build());

        // 여가/취미
        subList.add(SubCategory.builder()
                .subName("영화/공연")
                .goal(0)
                .user(user)
                .mainCategory(main3)
                .build());
        subList.add(SubCategory.builder()
                .subName("취미 용품")
                .goal(0)
                .user(user)
                .mainCategory(main3)
                .build());
        subList.add(SubCategory.builder()
                .subName("여행")
                .goal(0)
                .user(user)
                .mainCategory(main3)
                .build());

        subCategoryRepository.saveAll(subList);

    }

    public void initializeCategoryIncome(Long userId)
    {
        User user = User.builder().Id(userId).build();
        // 급여
        var main1 = mainCategoryRepository.save(MainCategory.builder()
                .mainName("급여")
                .categoryType(CategoryType.INCOME)
                .user(user)
                .build());
        // 투자수익
        var main2 = mainCategoryRepository.save(MainCategory.builder()
                .mainName("투자 수익")
                .categoryType(CategoryType.INCOME)
                .user(user)
                .build());
        // 기타 수익
        var main3 = mainCategoryRepository.save(MainCategory.builder()
                .mainName("기타 수익")
                .categoryType(CategoryType.INCOME)
                .user(user)
                .build());


        var subList = new ArrayList<SubCategory>();
        // 급여
        subList.add(SubCategory.builder()
                .subName("월급")
                .goal(0)
                .user(user)
                .mainCategory(main1)
                .build());
        subList.add(SubCategory.builder()
                .subName("투자 수익")
                .goal(0)
                .user(user)
                .mainCategory(main1)
                .build());
        subList.add(SubCategory.builder()
                .subName("기타 수익")
                .goal(0)
                .user(user)
                .mainCategory(main1)
                .build());

        // 투자 수익
        subList.add(SubCategory.builder()
                .subName("주식")
                .goal(0)
                .user(user)
                .mainCategory(main2)
                .build());
        subList.add(SubCategory.builder()
                .subName("예금 이자")
                .goal(0)
                .user(user)
                .mainCategory(main2)
                .build());
        subList.add(SubCategory.builder()
                .subName("부동산")
                .goal(0)
                .user(user)
                .mainCategory(main2)
                .build());

        // 기타 수익
        subList.add(SubCategory.builder()
                .subName("중고 거래")
                .goal(0)
                .user(user)
                .mainCategory(main3)
                .build());
        subList.add(SubCategory.builder()
                .subName("용돈")
                .goal(0)
                .user(user)
                .mainCategory(main3)
                .build());
        subList.add(SubCategory.builder()
                .subName("환불/환불")
                .goal(0)
                .user(user)
                .mainCategory(main3)
                .build());

        subCategoryRepository.saveAll(subList);
    }

    public void initializeAsset(Long userId)
    {
        User user = User.builder().Id(userId).build();
        // 결제수단
        var main1 = mainAssetRepository.save(MainAsset.builder()
                .mainName("결제 수단")
                .user(user)
                .build());
        // 은행 계좌
        var main2 = mainAssetRepository.save(MainAsset.builder()
                .mainName("은행 계좌")
                .user(user)
                .build());
        // 현금
        var main3 = mainAssetRepository.save(MainAsset.builder()
                .mainName("현금")
                .user(user)
                .build());

        var subList = new ArrayList<SubAsset>();
        // 결제수단
        subList.add(SubAsset.builder()
                .subName("신용카드")
                .user(user)
                .mainAsset(main1)
                .build());
        subList.add(SubAsset.builder()
                .subName("체크카드")
                .user(user)
                .mainAsset(main1)
                .build());
        subList.add(SubAsset.builder()
                .subName("선불카드")
                .user(user)
                .mainAsset(main1)
                .build());

        // 은행계좌
        subList.add(SubAsset.builder()
                .subName("급여 통장")
                .user(user)
                .mainAsset(main2)
                .build());
        subList.add(SubAsset.builder()
                .subName("저축 통장")
                .user(user)
                .mainAsset(main2)
                .build());
        subList.add(SubAsset.builder()
                .subName("CMA 계좌")
                .user(user)
                .mainAsset(main2)
                .build());

        // 현금
        subList.add(SubAsset.builder()
                .subName("현금")
                .user(user)
                .mainAsset(main3)
                .build());
        subList.add(SubAsset.builder()
                .subName("비상금")
                .user(user)
                .mainAsset(main3)
                .build());
        subList.add(SubAsset.builder()
                .subName("기타 현금")
                .user(user)
                .mainAsset(main3)
                .build());

        subAssetRepository.saveAll(subList);

    }


}
