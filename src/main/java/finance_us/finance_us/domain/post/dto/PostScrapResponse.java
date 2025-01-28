package finance_us.finance_us.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

public class PostScrapResponse {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostScrapResponseDTO{
        private Long postId;
        private Boolean isScraped;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime createdAt;
    }
}
