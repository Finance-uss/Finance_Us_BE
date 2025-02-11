package finance_us.finance_us.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponseDTO{
        private Long commentId;
        private LocalDateTime createdAt;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResultDTO{
        private Long commentId;
        private int commentCount;
        private List<CommentDTO> commentsList;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentDTO {
        private Long commentId;
        private Long userId;
        private String name;
        private Boolean isAuthenticated;
//        private String userImageUrl;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("createdAt")
        private LocalDateTime getCreatedAt() { // updatedAt이 없는 경우 createdAt 반환
            return !createdAt.isEqual(updatedAt) ? null : createdAt;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("updatedAt")
        private LocalDateTime getUpdatedAt() { // updatedAt이 있는 경우 updatedAt 반환
            return !createdAt.isEqual(updatedAt) ? updatedAt : null;
        }
    }
}
