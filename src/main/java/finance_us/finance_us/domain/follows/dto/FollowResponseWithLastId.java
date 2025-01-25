package finance_us.finance_us.domain.follows.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FollowResponseWithLastId {
    private List<FollowResponse> result;
    private Long lastId;
}
