package finance_us.finance_us.domain.post.converter;

import finance_us.finance_us.domain.post.dto.PostLikeResponse;
import finance_us.finance_us.domain.post.entity.PostLike;

public class PostLikeConverter {
    public static PostLikeResponse.PostLikeResponseDTO toPostLikeResponseDTO(PostLike postLike, Long likesCount) {
        return PostLikeResponse.PostLikeResponseDTO.builder()
                .postId(postLike.getPost().getId())
                .likesCount(likesCount)
                .createdAt(postLike.getCreatedAt())
                .build();
    }
}
