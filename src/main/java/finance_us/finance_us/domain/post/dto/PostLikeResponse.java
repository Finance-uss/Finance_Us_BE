package finance_us.finance_us.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

public class PostLikeResponse {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostLikeResponseDTO{
        private Long postId;
        private Long likesCount;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime createdAt;
    }
}
