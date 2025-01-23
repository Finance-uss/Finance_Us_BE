package finance_us.finance_us.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

public class CommentLikeResponse {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentLikeResponseDTO{
        private Long commentId;
        private Long likesCount;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime createdAt;
    }
}
