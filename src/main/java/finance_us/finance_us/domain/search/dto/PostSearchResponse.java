package finance_us.finance_us.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostSearchResponse {
    private List<PostResponse> posts;
    private Long lastId;
}
