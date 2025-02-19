package finance_us.finance_us.domain.comment.service;

import finance_us.finance_us.domain.comment.converter.CommentLikeConverter;
import finance_us.finance_us.domain.comment.dto.CommentLikeResponse;
import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.entity.CommentLike;
import finance_us.finance_us.domain.comment.repository.CommentLikeRepository;
import finance_us.finance_us.domain.comment.repository.CommentRepository;
import finance_us.finance_us.domain.notifications.entity.Notification;
import finance_us.finance_us.domain.notifications.service.NotificationService;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    //알림 서비스 추가
    private final NotificationService notificationService;

    // 댓글 좋아요 추가
    public CommentLikeResponse.CommentLikeResponseDTO likeComment(String token, Long commentId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // 댓글 유효성 검증
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("Comment not found"));
        
        // 삭제된 댓글 좋아요 불가
        if (comment.isDeleted()) {
            throw new IllegalStateException("Cannot like a deleted comment.");
        }

        // 사용자 유효성 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 이미 좋아요가 눌렸는지 확인
        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalArgumentException("You already liked this comment");
        }

        // 좋아요 저장
        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();

        commentLikeRepository.save(commentLike);

        Long likesCount = commentLikeRepository.countLikesByCommentId(commentId);

        // 알림 추가
        notificationService.addCommentLikeNotification(commentId, userId);

        return CommentLikeConverter.toCommentLikeResponseDTO(commentLike, likesCount);
    }

    // 댓글 좋아요 갯수 반환
    public CommentLikeResponse.CommentLikeResponseDTO getCommentLikes(String token, Long commentId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // 댓글 유효성 검증
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("Comment not found"));

        Long likesCount = commentLikeRepository.countLikesByCommentId(commentId);

        return CommentLikeConverter.toGetCommentLikesResponseDTO(commentId, likesCount);
    }
}
