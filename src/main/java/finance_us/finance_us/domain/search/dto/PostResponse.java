package finance_us.finance_us.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponse {
    private Long postId;
    private String title;
    private String content;
    private String author;
    private String boardType;
}
