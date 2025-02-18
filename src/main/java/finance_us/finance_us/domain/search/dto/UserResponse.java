package finance_us.finance_us.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String profileImageUrl;
    private String one_liner;
    private boolean isFollowed;
}
