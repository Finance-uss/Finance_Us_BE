package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.domain.account.dto.FollowResponse;
import finance_us.finance_us.domain.account.dto.ReportResponse;
import finance_us.finance_us.domain.account.dto.AccountRequest;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.entity.status.AccountType;
import finance_us.finance_us.domain.account.repository.AccountRepository;
import finance_us.finance_us.domain.category.entity.SubAsset;
import finance_us.finance_us.domain.category.entity.SubCategory;
import finance_us.finance_us.domain.category.repository.SubAssetRepository;
import finance_us.finance_us.domain.category.repository.SubCategoryRepository;
import finance_us.finance_us.domain.follows.entity.Follow;
import finance_us.finance_us.domain.follows.repository.FollowRepository;
import finance_us.finance_us.domain.statistics.service.CategoryStatisticsService;
import finance_us.finance_us.domain.statistics.service.PeriodStatisticsService;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final SubAssetRepository subAssetRepository;
    private final FollowRepository followRepository;
    private final TokenProvider tokenProvider;
    //통계 테이블 관리 로직을 위해 추가
    private final CategoryStatisticsService categoryStatisticsService;
    private final PeriodStatisticsService periodStatisticsService;


    // 가계부 생성
    public Account createAccount(AccountRequest.AccountRequestDTO request, String token) {

        // userId 추출
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // ID로 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // SubCategory 조회
        SubCategory subCategory = subCategoryRepository.findBySubNameAndUserId(request.getSubName(), userId)
                .orElseThrow(() -> new IllegalArgumentException("SubCategory not found"));


        // SubCategory 조회
        SubAsset subAsset = subAssetRepository.findBySubNameAndUserId(request.getSubAssetName(), userId)
                .orElseThrow(() -> new IllegalArgumentException("SubAsset not found"));

        System.out.println(request);

        // account 생성
        Account account = Account.builder()
                .accountType(AccountType.valueOf(request.getAccountType()))
                .date(request.getDate())
                .amount(request.getAmount())
                .title(request.getTitle())
                .status(request.getStatus())
                .score(request.getScore())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .imageName(request.getImageName())
                .subCategory(subCategory)
                .subAsset(subAsset)
                .user(user)
                .build();

        //통계 업데이트
        categoryStatisticsService.updateStatisticsOnCreate(token, account);
        periodStatisticsService.updatePeriodStatisticsOnCreate(token, account);

        System.out.println(account);

        return accountRepository.save(account);
    }


    // 가계부 수정
    public Account updateAccount(Long accountId, AccountRequest.AccountRequestDTO request, String token) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // 기존 계좌 조회
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        //통계에 사용할 수정 전 계좌 깊은 복사
        Account oldAccount = Account.builder()
                .id(account.getId())
                .accountType(account.getAccountType())
                .date(account.getDate())
                .amount(account.getAmount())
                .title(account.getTitle())
                .status(account.getStatus())
                .score(account.getScore())
                .content(account.getContent())
                .imageUrl(account.getImageUrl())
                .subCategory(account.getSubCategory())
                .subAsset(account.getSubAsset())
                .user(account.getUser())
                .build();

        // SubCategory 조회
        SubCategory subCategory = subCategoryRepository.findBySubNameAndUserId(request.getSubName(), userId)
                .orElseThrow(() -> new IllegalArgumentException("SubCategory not found"));

        // SubAsset 조회
        SubAsset subAsset = subAssetRepository.findBySubNameAndUserId(request.getSubAssetName(), userId)
                .orElseThrow(() -> new IllegalArgumentException("SubAsset not found"));

        // 필드 업데이트
        account.setAccountType(AccountType.valueOf(request.getAccountType()));
        account.setDate(request.getDate());
        account.setAmount(request.getAmount());
        account.setTitle(request.getTitle());
        account.setStatus(request.getStatus());
        account.setScore(request.getScore());
        account.setContent(request.getContent());
        account.setImageUrl(request.getImageUrl());
        account.setImageName(request.getImageName());
        account.setSubCategory(subCategory);
        account.setSubAsset(subAsset);

        //통계 업데이트
        categoryStatisticsService.updateStatisticsOnUpdate(token, oldAccount, account);
        periodStatisticsService.updatePeriodStatisticsOnUpdate(token, oldAccount, account);

        return accountRepository.save(account);
    }

    // 가계부 삭제
    public void deleteAccount(Long accountId, String token) {
        tokenProvider.extractUserIdFromToken(token);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        //통계 업데이트
        categoryStatisticsService.updateStatisticsOnDelete(token, account);
        periodStatisticsService.updatePeriodStatisticsOnDelete(token, account);

        accountRepository.delete(account);
    }

    // 가계부 레포트 조회
    public ReportResponse getReport(Integer year, Integer month, String token) {
        // userId 추출
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // 해당 월의 Account 데이터 조회
        List<Account> accounts = accountRepository.findByUserIdAndYearAndMonth(userId, year, month);

        // 데이터를 점수에 따라 그룹화
        List<ReportResponse.ReportResponseDTO> reduceActivity = new ArrayList<>();
        List<ReportResponse.ReportResponseDTO> satisfactoryActivity = new ArrayList<>();
        List<ReportResponse.ReportResponseDTO> maintainActivity = new ArrayList<>();

        for (Account account : accounts) {
            ReportResponse.ReportResponseDTO dto = new ReportResponse.ReportResponseDTO(
                    account.getId(),
                    account.getScore(),
                    account.getTitle(),
                    account.getAmount(),
                    account.getDate(),
                    account.getSubCategory().getSubName(),
                    account.getImageUrl()
            );

            // 점수별로 분류
            if (account.getScore() <= 2) {
                reduceActivity.add(dto);
            } else if (account.getScore() == 3) {
                maintainActivity.add(dto);
            } else if (account.getScore() >= 4) {
                satisfactoryActivity.add(dto);
            }
        }

        // 결과 반환
        return new ReportResponse(reduceActivity, satisfactoryActivity, maintainActivity);
    }

    // 가계부 특정 팔로우 조회
    public FollowResponse getFollow(Long followId, String token) {
        tokenProvider.extractUserIdFromToken(token);
        // Follow 객체 조회
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new IllegalArgumentException("Follow not found"));

        // followingId 조회
        Long followingId = follow.getFollowingId();

        // name 가져오기
        User User = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String name = User.getName();

        // 현재 날짜 불러오기
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // 가계부 데이터 조회 (이번 달)
        List<Account> accounts = accountRepository.findAccountsByYearAndMonth(followingId, currentYear, currentMonth);

        // 소분류 목표금액 조회
        List<SubCategory> subCategories = subCategoryRepository.findByUserId(followingId);

       // 소비량 계산
        Object expenseRate;
        if (subCategories.isEmpty()) {
            expenseRate = "목표 금액이 설정되지 않았어요. 함께 응원하며 기다려볼까요?";
        } else {
            // 목표 금액 합산
            int totalGoal = subCategories.stream()
                    .mapToInt(SubCategory::getGoal)
                    .sum();

            long totalAmount = accounts.stream()
                    .mapToLong(Account::getAmount)
                    .sum();

            // 총 소비량이 크면 0으로 설정
            if (totalAmount > totalGoal) {
                expenseRate = 0;
            } else {
                // 계산된 퍼센트 반환
                int calculatedRate = (int) ((totalAmount * 100) / totalGoal);
                expenseRate = calculatedRate;
            }
        }

        // DTO 리스트 생성
        List<FollowResponse.FollowResponseDTO> accountDTOs = new ArrayList<>();
        for (Account account : accounts) {
            FollowResponse.FollowResponseDTO dto = new FollowResponse.FollowResponseDTO(
                    account.getId(),
                    account.getScore(),
                    account.getTitle(),
                    account.getAmount(),
                    account.getDate(),
                    account.getSubCategory().getSubName(),
                    account.getImageUrl(),
                    account.getTotalLike(),
                    account.getTotalCheer()
            );
            accountDTOs.add(dto);
        }

        return new FollowResponse(name, expenseRate, accountDTOs);
    }



}
