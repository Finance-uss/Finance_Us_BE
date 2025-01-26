package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.domain.account.dto.LikeRequest;
import finance_us.finance_us.domain.account.dto.LikeResponse;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.entity.AccountLike;
import finance_us.finance_us.domain.account.repository.AccountRepository;
import finance_us.finance_us.domain.account.repository.LikeRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final LikeRepository likeRepository;


    public LikeResponse.LikeResponseDTO createLike(LikeRequest.LikeRequestDTO request, Authentication auth) {
        // userId 추출
        Long userId = (Long) auth.getPrincipal();

        // userId로 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // accountId로 가계부 조회
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

       // 중복 좋아요 체크
        Optional<AccountLike> existingLike = likeRepository.findByUserIdAndAccountId(userId, request.getAccountId());
        if (existingLike.isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누르셨습니다.");
        }

        // 좋아요 생성
        AccountLike like = AccountLike.builder()
                .user(user)
                .account(account)
                .build();
        likeRepository.save(like);

        // likeTotal 증가
        account.setTotalLike(account.getTotalLike() + 1);
        accountRepository.save(account);

        // 응답 생성
        return LikeResponse.LikeResponseDTO.builder()
                .accountId(account.getId())
                .totalLike(account.getTotalLike())
                .build();

    }
}
