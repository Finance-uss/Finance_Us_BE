package finance_us.finance_us.domain.comment.service;

import finance_us.finance_us.domain.comment.converter.CommentConverter;
import finance_us.finance_us.domain.comment.dto.CommentRequest;
import finance_us.finance_us.domain.comment.dto.CommentResponse;
import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.repository.CommentLikeRepository;
import finance_us.finance_us.domain.comment.repository.CommentRepository;
import finance_us.finance_us.domain.notifications.service.NotificationService;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    //알림 서비스 추가
    private final NotificationService notificationService;

    // 댓글 생성
    public Comment createComment(String token, Long postId, CommentRequest.CommentRequestDTO request) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // 게시글 유효성 검증
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));
        
        // 사용자 유효성 검증
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("User not found"));

        // 대댓글
        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

            // 삭제된 댓글에 대댓글 생성 불가
            if (parentComment.isDeleted()) {
                throw new IllegalStateException("Cannot reply to a deleted comment.");
            }

        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(user)
                .parentComment(parentComment)
                .build();

        // 알림 추가
        //부모 댓글이 있는 경우 → 부모 댓글 작성자에게만 알림 전송 (게시글 주인에게는 알림 X)
        if (parentComment != null) {
            notificationService.addReplyNotification(parentComment.getId(), userId);
        }
        //부모 댓글이 없는 경우 → 게시글 주인에게 댓글 알림 전송
        else {
            notificationService.addCommentNotification(postId, userId);
        }

        return commentRepository.save(comment);
    }

    // 댓글 수정
    public Comment updateComment(String token, Long commentId, CommentRequest.CommentUpdateDTO request) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("Comment not found"));
    
        // 삭제된 댓글 수정 불가
        if (comment.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted comment.");
        }

        // 작성자 검증
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to update this comment.");
        }

        comment.setContent(request.getContent());

        return commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(String token, Long commentId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("Comment not found"));

        // 삭제된 댓글 삭제 불가
        if (comment.isDeleted()) {
            throw new IllegalStateException("Cannot delete a deleted comment.");
        }

        // 작성자 검증
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this comment.");
        }

        comment.delete();
        commentRepository.save(comment);
    }

    // 댓글 목록 반환
    public List<Comment> getCommentsByPost(String token, Long postId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    // 댓글 갯수 반환
    public int getCommentCount(String token, Long postId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        return commentRepository.countByPostId(postId);
    }
    
    // 최상위 댓글과 그에 대한 대댓글을 트리 형태로 구성
    public List<CommentResponse.CommentDTO> getCommentsByPostWithReplies(String token, Long postId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        
        List<Comment> comments = getCommentsByPost(token, postId);
        
        Map<Long, CommentResponse.CommentDTO> commentDTOMap = new HashMap<>(); // commentId를 키로, CommentDTO 객체를 값으로 저장
        List<CommentResponse.CommentDTO> topLevelComments = new ArrayList<>(); // 부모가 없는 댓글들 따로 저장

        for (Comment comment : comments) {
            boolean isMine = comment.getUser().getId().equals(userId); // 내가 작성한 댓글인지 확인
            boolean isLiked = commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), userId);

            CommentResponse.CommentDTO dto = CommentConverter.toCommentDTO(comment);
            dto.setIsMine(isMine); // 내가 작성한 댓글인지 설정
            dto.setIsLiked(isLiked); // 좋아요 여부 설정
            commentDTOMap.put(dto.getCommentId(), dto);

            if (comment.getParentComment() == null) {
                topLevelComments.add(dto); // 부모 댓글이 없으면 최상위 댓글 리스트에 추가
            }
        }

        // 대댓글을 부모 댓글의 `replies` 리스트에 추가
        for (Comment comment : comments) {
            if (comment.getParentComment() != null) {
                CommentResponse.CommentDTO parentDTO = commentDTOMap.get(comment.getParentComment().getId());
                if (parentDTO != null) {
                    parentDTO.getReplies().add(commentDTOMap.get(comment.getId())); // 대댓글 추가
                }
            }
        }

        return topLevelComments;
    }

}
