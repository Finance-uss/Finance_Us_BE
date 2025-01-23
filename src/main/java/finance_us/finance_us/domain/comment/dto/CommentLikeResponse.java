package finance_us.finance_us.domain.comment.dto;

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
        private LocalDateTime createdAt;
    }
}
