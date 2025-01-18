package finance_us.finance_us.domain.comment.converter;

import finance_us.finance_us.domain.comment.dto.CommentResponse;
import finance_us.finance_us.domain.comment.entity.Comment;

public class CommentConverter {
    public static CommentResponse.CommentResponseDTO toCommentResponseDTO(Comment comment) {
        return CommentResponse.CommentResponseDTO.builder()
                .commentId(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
