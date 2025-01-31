package finance_us.finance_us.domain.follows.service;

import finance_us.finance_us.domain.follows.dto.FollowResponse;
import finance_us.finance_us.domain.follows.dto.FollowResponseWithLastId;
import finance_us.finance_us.domain.follows.entity.Follow;
import finance_us.finance_us.domain.follows.repository.FollowRepository;
import finance_us.finance_us.domain.notifications.service.NotificationService;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.security.TokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final TokenProvider tokenProvider;

    @Transactional
    public void addFollow(String token, Long followingId){
        Long userId = tokenProvider.extractUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        boolean alreadyFollowing = followRepository.existsByUserIdAndFollowingId(userId, followingId);
        if (alreadyFollowing) {
            throw new GeneralException(ErrorStatus.ALREADY_FOLLOW);
        }

        Follow follow = Follow.builder()
                .user(user)
                .followingId(followingId)
                .build();
        followRepository.save(follow);

        //알림테이블에 알림 추가

        notificationService.addFollowNotification(followingId, userId);
    }

    @Transactional
    public void removeFollow(String token, Long followingId){
        Long userId = tokenProvider.extractUserIdFromToken(token);
        boolean alreadyFollowing = followRepository.existsByUserIdAndFollowingId(userId, followingId);
        if (!alreadyFollowing) {
            throw new GeneralException(ErrorStatus.FOLLOW_NOT_FOUND);
        }
        followRepository.deleteByUserIdAndFollowingId(userId, followingId);
    }

    public FollowResponseWithLastId getFollows(String token, Long lastFollowingId, int size) {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        lastFollowingId = (lastFollowingId == null) ? 0 : lastFollowingId;

        PageRequest pageRequest = PageRequest.of(0, size);

        List<Follow> follows = followRepository.findByUserIdAndIdGreaterThan(userId, lastFollowingId, pageRequest);

        List<FollowResponse> response = follows.stream()
                .map(follow -> {
                    User followingUser = userRepository.findById(follow.getFollowingId())
                            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

                    return new FollowResponse(
                            follow.getFollowingId(),
                            followingUser.getName()
                    );
                })
                .collect(Collectors.toList());

        Long lastId = follows.isEmpty() ? null : follows.get(follows.size() -1).getFollowingId();

        return new FollowResponseWithLastId(response, lastId);
    }
}
