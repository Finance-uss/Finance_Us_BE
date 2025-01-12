package finance_us.finance_us.domain.post.dto;

import finance_us.finance_us.domain.post.entity.status.Category;
import finance_us.finance_us.domain.post.entity.status.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostRequest {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostRequestDTO {
        private String title;
        private String content;
        private String postType;
        private String category;
        private String imageUrl;
    }
}
