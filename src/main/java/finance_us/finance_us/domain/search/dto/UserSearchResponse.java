package finance_us.finance_us.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserSearchResponse {
    private List<UserResponse> users;
    private Long lastId;
}