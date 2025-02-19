package finance_us.finance_us.domain.notifications.dto;

import finance_us.finance_us.domain.notifications.entity.Notification;
import finance_us.finance_us.domain.notifications.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotificationListResponse {
    private List<NotificationResponse> notifications; // 알림 목록
    private Long lastNotificationId; // 마지막 알림 ID

    public static NotificationListResponse fromEntities(List<Notification> notifications, NotificationService notificationService) {
        // 마지막 알림 ID를 첫 번째로 추출
        Long lastNotificationId = notifications.isEmpty() ? null : notifications.get(notifications.size() - 1).getId();

        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(notification -> {
                    String resourceTitle = notificationService.getResourceTitle(notification.getResourceType(), notification.getResourceId());
                    return NotificationResponse.fromEntity(notification, resourceTitle);
                })
                .toList();

        return new NotificationListResponse(notificationResponses, lastNotificationId);
    }
}
