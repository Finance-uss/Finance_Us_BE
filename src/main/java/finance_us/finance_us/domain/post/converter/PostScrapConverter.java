package finance_us.finance_us.domain.post.converter;

import finance_us.finance_us.domain.post.dto.PostScrapResponse;
import finance_us.finance_us.domain.post.entity.PostScrap;

public class PostScrapConverter {
    public static PostScrapResponse.PostScrapResponseDTO toPostScrapResponseDTO(PostScrap postScrap, Boolean isScraped) {
        return PostScrapResponse.PostScrapResponseDTO.builder()
                .postId(postScrap.getPost().getId())
                .isScraped(isScraped)
                .createdAt(isScraped ? postScrap.getCreatedAt() : null)
                .build();
    }
}
