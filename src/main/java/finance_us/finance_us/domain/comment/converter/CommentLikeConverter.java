package finance_us.finance_us.domain.comment.converter;

import finance_us.finance_us.domain.comment.dto.CommentLikeResponse;
import finance_us.finance_us.domain.comment.entity.CommentLike;

public class CommentLikeConverter {
    public static CommentLikeResponse.CommentLikeResponseDTO toCommentLikeResponseDTO(CommentLike commentLike, Long likesCount) {
        return CommentLikeResponse.CommentLikeResponseDTO.builder()
                .commentId(commentLike.getComment().getId())
                .likesCount(likesCount)
                .createdAt(commentLike.getCreatedAt())
                .build();
    }

    public static CommentLikeResponse.CommentLikeResponseDTO toGetCommentLikesResponseDTO(Long commentId, Long likesCount) {
        return CommentLikeResponse.CommentLikeResponseDTO.builder()
                .commentId(commentId)
                .likesCount(likesCount)
                .build();
    }
}
