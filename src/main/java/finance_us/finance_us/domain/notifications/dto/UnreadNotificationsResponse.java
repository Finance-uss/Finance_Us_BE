package finance_us.finance_us.domain.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnreadNotificationsResponse {
    private boolean hasUnread;
}
