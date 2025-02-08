package finance_us.finance_us.domain.post.converter;

import finance_us.finance_us.domain.post.dto.PostResponse;
import finance_us.finance_us.domain.post.entity.Post;

import java.util.List;

public class PostConverter {
    public static PostResponse.PostResponseDTO toPostResponseDTO(Post post) {
        return PostResponse.PostResponseDTO.builder()
                .postId(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public static PostResponse.PostListDto toPostListDto(Post post, Long likeCnt, int commentCnt) {
        return PostResponse.PostListDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imgUrl(post.getImageUrl())
                .category(post.getCategory().toString())
                .likeCnt(likeCnt)
                .commentCnt(commentCnt)
                .build();

    }

}
