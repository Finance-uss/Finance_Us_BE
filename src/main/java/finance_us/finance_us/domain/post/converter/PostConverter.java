package finance_us.finance_us.domain.post.converter;

import finance_us.finance_us.domain.post.dto.PostResponse;
import finance_us.finance_us.domain.post.entity.Post;

public class PostConverter {
    public static PostResponse.PostResponseDTO toPostResponseDTO(Post post) {
        return PostResponse.PostResponseDTO.builder()
                .postId(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
