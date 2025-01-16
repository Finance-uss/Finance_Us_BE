package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.domain.account.dto.AccountRequest;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.entity.status.AccountType;
import finance_us.finance_us.domain.account.repository.AccountRepository;
import finance_us.finance_us.domain.category.entity.SubAsset;
import finance_us.finance_us.domain.category.entity.SubCategory;
import finance_us.finance_us.domain.category.repository.SubAssetRepository;
import finance_us.finance_us.domain.category.repository.SubCategoryRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final SubAssetRepository subAssetRepository;

    // 가계부 생성
    public Account createAccount(AccountRequest.AccountRequestDTO request) {

        // 임의로 1번 사용자 조회 (수정 필요)
        User user = userRepository.findById(1L).get();

        // SubCategory 조회
        SubCategory subCategory = subCategoryRepository.findBySubName(request.getSubName())
                .orElseThrow(() -> new IllegalArgumentException("SubCategory not found"));

        // SubCategory 조회
        SubAsset subAsset = subAssetRepository.findBySubName(request.getSubAssetName())
                .orElseThrow(() -> new IllegalArgumentException("SubAsset not found"));

        // account 생성
        Account account = Account.builder()
                .accountType(AccountType.valueOf(request.getAccountType()))
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
}
