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
            @RequestParam(required = false) Long lastNotificationId,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Long userId
            //@RequestHeader("Authorization") String token
    ) {
        NotificationListResponse response = notificationService.getNotifications(lastNotificationId, size, userId);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/unread")
    public ApiResponse<UnreadNotificationsResponse> hasUnreadNotifications(
            @RequestParam Long userId
            //@RequestHeader("Authorization") String token
    ){
        UnreadNotificationsResponse response = notificationService.hasUnreadNotifications(userId);
        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/{notificationId}")
    public ApiResponse<Void> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId
            //@RequestHeader("Authorization") String token
    ){
        notificationService.markAsRead(notificationId, userId);
        return ApiResponse.onSuccess(null);
    }
}
