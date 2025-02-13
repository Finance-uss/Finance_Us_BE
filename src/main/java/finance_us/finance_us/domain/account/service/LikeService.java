package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.domain.account.dto.CheerRequest;
import finance_us.finance_us.domain.account.dto.CheerResponse;
import finance_us.finance_us.domain.account.dto.LikeRequest;
import finance_us.finance_us.domain.account.dto.LikeResponse;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.entity.AccountCheer;
import finance_us.finance_us.domain.account.entity.AccountLike;
import finance_us.finance_us.domain.account.repository.AccountRepository;
import finance_us.finance_us.domain.account.repository.CheerRepository;
import finance_us.finance_us.domain.account.repository.LikeRepository;
import finance_us.finance_us.domain.notifications.service.NotificationService;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final LikeRepository likeRepository;
    private final CheerRepository cheerRepository;
    private final TokenProvider tokenProvider;
    //알림 서비스 추가
    private final NotificationService notificationService;


    public LikeResponse.LikeResponseDTO createLike(LikeRequest.LikeRequestDTO request, String token) {
        // userId 추출
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // userId로 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // accountId로 가계부 조회
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

       // 중복 좋아요 체크
        Optional<AccountLike> existingLike = likeRepository.findByUserIdAndAccountId(userId, request.getAccountId());
        if (existingLike.isPresent()) {
            throw new GeneralException(ErrorStatus.ALREADY_LIKE);
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

        //알림 추가
        notificationService.addEmojiNotification(request.getAccountId(), userId);

        // 응답 생성
        return LikeResponse.LikeResponseDTO.builder()
                .accountId(account.getId())
                .totalLike(account.getTotalLike())
                .build();

    }

    // 응원해요 추가
    public CheerResponse.CheerResponseDTO createCheer(CheerRequest.CheerRequestDTO request, String token) {
        // userId 추출
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // userId로 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // accountId로 가계부 조회
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.ACCOUNT_NOT_FOUND));

        // 중복 응원해요 체크
        Optional<AccountCheer> existingLike = cheerRepository.findByUserIdAndAccountId(userId, request.getAccountId());
        if (existingLike.isPresent()) {
            throw new GeneralException(ErrorStatus.ALREADY_CHEER);
        }

        // 좋아요 생성
        AccountCheer cheer = AccountCheer.builder()
                .user(user)
                .account(account)
                .build();
        cheerRepository.save(cheer);

        // likeTotal 증가
        account.setTotalCheer(account.getTotalCheer() + 1);
        accountRepository.save(account);

        //알림 추가
        notificationService.addEmojiNotification(request.getAccountId(), userId);

        // 응답 생성
        return CheerResponse.CheerResponseDTO.builder()
                .accountId(account.getId())
                .totalCheer(account.getTotalCheer())
                .build();
    }

}
