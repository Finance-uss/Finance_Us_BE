package finance_us.finance_us.domain.comment.service;

import finance_us.finance_us.domain.comment.dto.CommentRequest;
import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.repository.CommentRepository;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 생성
    public Comment createComment(Long postId, CommentRequest.CommentRequestDTO request) {
        // 게시글 유효성 검증
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));
        
        // 사용자 유효성 검증
//        User user = userRepository.findById(userId)
//                .orElseThrow(()->new IllegalArgumentException("User not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .post(post)
                //.user(user)
                .build();

        return commentRepository.save(comment);
    }

    // 댓글 수정
    public Comment updateComment(Long commentId, CommentRequest.CommentRequestDTO request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("Comment not found"));

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("Comment not found"));

        commentRepository.delete(comment);
    }
}
