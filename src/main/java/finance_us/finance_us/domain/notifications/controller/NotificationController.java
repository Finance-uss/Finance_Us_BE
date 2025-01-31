package finance_us.finance_us.domain.notifications.controller;

import finance_us.finance_us.domain.notifications.dto.NotificationListResponse;
import finance_us.finance_us.domain.notifications.dto.NotificationResponse;
import finance_us.finance_us.domain.notifications.dto.UnreadNotificationsResponse;
import finance_us.finance_us.domain.notifications.service.NotificationService;
import finance_us.finance_us.global.ApiResponse;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final TokenProvider tokenProvider;

    @GetMapping
    public ApiResponse<NotificationListResponse> getNotifications(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Long lastNotificationId,
            @RequestParam(defaultValue = "10") int size
    ) {
        NotificationListResponse response = notificationService.getNotifications(token, lastNotificationId, size);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/unread")
    public ApiResponse<UnreadNotificationsResponse> hasUnreadNotifications(
            @RequestHeader("Authorization") String token
    ){
        UnreadNotificationsResponse response = notificationService.hasUnreadNotifications(token);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/{notificationId}")
    public ApiResponse<Void> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long notificationId
    ){
        notificationService.markAsRead(token, notificationId);
        return ApiResponse.onSuccess(null);
    }
}
