package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.domain.account.dto.AccountFollowResponse;
import finance_us.finance_us.domain.account.dto.AccountReportResponse;
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
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final SubAssetRepository subAssetRepository;
    private final FollowRepository followRepository;

    // 가계부 생성
    public Account createAccount(AccountRequest.AccountRequestDTO request, Authentication auth) {

        // 사용자 ID 가져오기
        Long userId;
        if (auth.getPrincipal() instanceof UserDetails) {
            userId = Long.valueOf(((UserDetails) auth.getPrincipal()).getUsername());
        } else {
            userId = Long.valueOf(auth.getPrincipal().toString());
        }

        // ID로 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // SubCategory 조회
        SubCategory subCategory = subCategoryRepository.findBySubName(request.getSubName())
                .orElseThrow(() -> new IllegalArgumentException("SubCategory not found"));

        // SubCategory 조회
        SubAsset subAsset = subAssetRepository.findBySubName(request.getSubAssetName())
                .orElseThrow(() -> new IllegalArgumentException("SubAsset not found"));

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
                .subCategory(subCategory)
                .subAsset(subAsset)
                .user(user)
                .build();

        return accountRepository.save(account);
    }


    // 가계부 수정
    public Account updateAccount(Long accountId, AccountRequest.AccountRequestDTO request) {

        // 기존 계좌 조회
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // SubCategory 조회
        SubCategory subCategory = subCategoryRepository.findBySubName(request.getSubName())
                .orElseThrow(() -> new IllegalArgumentException("SubCategory not found"));

        // SubAsset 조회
        SubAsset subAsset = subAssetRepository.findBySubName(request.getSubAssetName())
                .orElseThrow(() -> new IllegalArgumentException("SubAsset not found"));

        // 필드 업데이트
        account.setAccountType(AccountType.valueOf(request.getAccountType()));
        account.setAmount(request.getAmount());
        account.setTitle(request.getTitle());
        account.setStatus(request.getStatus());
        account.setScore(request.getScore());
        account.setContent(request.getContent());
        account.setImageUrl(request.getImageUrl());
        account.setSubCategory(subCategory);
        account.setSubAsset(subAsset);

        return accountRepository.save(account);
    }

    // 가계부 삭제
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        accountRepository.delete(account);
    }

    // 가계부 레포트 조회
    public AccountReportResponse getReport(Integer year, Integer month, Authentication authentication) {
        // userId 추출
        Long userId = (Long) authentication.getPrincipal();

        // 해당 월의 Account 데이터 조회
        List<Account> accounts = accountRepository.findByUserIdAndYearAndMonth(userId, year, month);

        // 데이터를 점수에 따라 그룹화
        List<AccountReportResponse.AccountReportResponseDTO> reduceActivity = new ArrayList<>();
        List<AccountReportResponse.AccountReportResponseDTO> satisfactoryActivity = new ArrayList<>();
        List<AccountReportResponse.AccountReportResponseDTO> maintainActivity = new ArrayList<>();

        for (Account account : accounts) {
            AccountReportResponse.AccountReportResponseDTO dto = new AccountReportResponse.AccountReportResponseDTO(
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
        return new AccountReportResponse(reduceActivity, satisfactoryActivity, maintainActivity);
    }

    // 가계부 특정 팔로우 조회
    public AccountFollowResponse getFollow(Long followId) {

        // Follow 객체 조회
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new IllegalArgumentException("Follow not found")); // Follow가 없으면 예외 처리

        // followingId 조회
        Long followingId = follow.getFollowingId();

        // name 가져오기
        User User = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")); // User가 없으면 예외 처리

        String name = User.getName();

        // 현재 날짜 불러오기
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // 가계부 데이터 조회
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
        List<AccountFollowResponse.AccountFollowResponseDTO> accountDTOs = new ArrayList<>();
        for (Account account : accounts) {
            AccountFollowResponse.AccountFollowResponseDTO dto = new AccountFollowResponse.AccountFollowResponseDTO(
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

        return new AccountFollowResponse(name, expenseRate, accountDTOs);
    }

}
