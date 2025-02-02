package finance_us.finance_us.domain.comment.service;

import finance_us.finance_us.domain.comment.dto.CommentRequest;
import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.repository.CommentRepository;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    // 댓글 생성
    public Comment createComment(String token, Long postId, CommentRequest.CommentRequestDTO request) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // 게시글 유효성 검증
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));
        
        // 사용자 유효성 검증
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("User not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .user(user)
                .build();

        return commentRepository.save(comment);
    }

    // 댓글 수정
    public Comment updateComment(String token, Long commentId, CommentRequest.CommentRequestDTO request) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("Comment not found"));

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

        // 작성자 검증
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this comment.");
        }

        commentRepository.delete(comment);
    }

    // 댓글 목록 반환
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    // 댓글 갯수 반환
    public int getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

}
