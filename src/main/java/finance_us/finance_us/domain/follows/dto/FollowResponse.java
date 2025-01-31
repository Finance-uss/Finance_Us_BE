package finance_us.finance_us.domain.follows.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowResponse {
    private Long followingId;
    private String name;
    //private String profileImage;
}
