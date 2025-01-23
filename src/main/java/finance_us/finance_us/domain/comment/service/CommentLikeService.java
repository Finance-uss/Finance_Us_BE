package finance_us.finance_us.domain.comment.service;

import finance_us.finance_us.domain.comment.converter.CommentConverter;
import finance_us.finance_us.domain.comment.converter.CommentLikeConverter;
import finance_us.finance_us.domain.comment.dto.CommentLikeResponse;
import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.entity.CommentLike;
import finance_us.finance_us.domain.comment.repository.CommentLikeRepository;
import finance_us.finance_us.domain.comment.repository.CommentRepository;
import finance_us.finance_us.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    // 댓글 좋아요 추가
    public CommentLikeResponse.CommentLikeResponseDTO likeComment(Long commentId, User user) {
        // 댓글 유효성 검증
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("Comment not found"));

        // 이미 좋아요가 눌렸는지 확인
        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            throw new IllegalArgumentException("You already liked this comment");
        }

        // 좋아요 저장
        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();

        commentLikeRepository.save(commentLike);

        Long likesCount = commentLikeRepository.countLikesByCommentId(commentId);

        return CommentLikeConverter.toCommentLikeResponseDTO(commentLike, likesCount);
    }
}
