package finance_us.finance_us.domain.notifications.service;

import finance_us.finance_us.domain.notifications.dto.NotificationListResponse;
import finance_us.finance_us.domain.notifications.dto.NotificationResponse;
import finance_us.finance_us.domain.notifications.dto.UnreadNotificationsResponse;
import finance_us.finance_us.domain.notifications.entity.Notification;
import finance_us.finance_us.domain.notifications.entity.status.Type;
import finance_us.finance_us.domain.notifications.repository.NotificationRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(String token, Long lastNotificationId, int size) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        List<Notification> notifications;
        PageRequest pageRequest = PageRequest.of(0, size);

        if (lastNotificationId == null) {
            // 첫 조회: 최신 알림부터 가져오기
            notifications = notificationRepository.findTopByUserIdAAndIsReadFalseOrderByCreatedAtDesc(userId, pageRequest);
        } else {
            // 스크롤: 특정 ID 이후의 알림 가져오기
            notifications = notificationRepository.findByUserIdAndIdANDIsReadFalseLessThanOrderByCreatedAtDesc(userId, lastNotificationId, pageRequest);
        }

        return NotificationListResponse.fromEntities(notifications);
    }

    @Transactional
    public void markAsRead(String token, Long notificationId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));

        if(!notification.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        if(notification.getIsRead()) {
            throw new GeneralException(ErrorStatus.NOTIFICATION_ALREADY_READ);
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public UnreadNotificationsResponse hasUnreadNotifications(String token) {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        boolean hasUnread = notificationRepository.existsUnreadNotificationsForUserId(userId);
        return new UnreadNotificationsResponse(hasUnread);
    }

    public void addFollowNotification(Long targetUserId, Long followerId){
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String message = follower.getName() + "님이 팔로우 했습니다.";

        Notification notification = Notification.builder()
                .type(Type.FOLLOW)
                .message(message)
                .isRead(false)
                .user(targetUser)
                .build();

        notificationRepository.save(notification);

    }
}
