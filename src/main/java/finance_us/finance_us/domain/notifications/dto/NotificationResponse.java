package finance_us.finance_us.domain.notifications.dto;

import finance_us.finance_us.domain.notifications.entity.Notification;
import finance_us.finance_us.domain.notifications.entity.status.ResourceType;
import finance_us.finance_us.domain.notifications.entity.status.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Type type;
    private String message;
    private ResourceType resourceType;
    private Long resourceId;
    private Boolean isRead;

    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.getResourceType(),
                notification.getResourceId(),
                notification.getIsRead()
        );
    }
}
