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

    public Account createAccount(AccountRequest.AccountRequestDTO request) {

        // 임의로 1번 사용자 조회 (수정 필요)
        User user = userRepository.findById(1L);

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
}
