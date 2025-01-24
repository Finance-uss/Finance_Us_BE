package finance_us.finance_us.domain.notifications.service;

import finance_us.finance_us.domain.notifications.dto.NotificationListResponse;
import finance_us.finance_us.domain.notifications.dto.NotificationResponse;
import finance_us.finance_us.domain.notifications.dto.UnreadNotificationsResponse;
import finance_us.finance_us.domain.notifications.entity.Notification;
import finance_us.finance_us.domain.notifications.repository.NotificationRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
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

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long lastNotificationId, int size, Long userId) {
        List<Notification> notifications;
        PageRequest pageRequest = PageRequest.of(0, size);

        if (lastNotificationId == null) {
            // 첫 조회: 최신 알림부터 가져오기
            notifications = notificationRepository.findTopByUserIdAAndIsReadFalseOrderByCreatedAtDescOrderByCreatedAtDesc(userId, pageRequest);
        } else {
            // 스크롤: 특정 ID 이후의 알림 가져오기
            notifications = notificationRepository.findByUserIdAndIdANDIsReadFalseLessThanOrderByCreatedAtDesc(userId, lastNotificationId, pageRequest);
        }

        return NotificationListResponse.fromEntities(notifications);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
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
    public UnreadNotificationsResponse hasUnreadNotifications(Long userId) {
        boolean hasUnread = notificationRepository.existsUnreadNotificationsForUserId(userId);
        return new UnreadNotificationsResponse(hasUnread);
    }
}
