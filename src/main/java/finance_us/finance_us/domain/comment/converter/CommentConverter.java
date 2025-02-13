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

    public static CommentResponse.CommentDTO toCommentDTO(Comment comment) {
        return CommentResponse.CommentDTO.builder()
                .commentId(comment.getId())
                .userId(comment.getUser().getId())
                .name(comment.getUser().getName())
                .isAuthenticated(comment.getUser().isAuthenticated())
                .userImageUrl(comment.getUser().getImage())
                .userImageName(comment.getUser().getImageName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
