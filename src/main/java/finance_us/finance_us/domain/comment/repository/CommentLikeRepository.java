package finance_us.finance_us.domain.comment.repository;

import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.entity.CommentLike;
import finance_us.finance_us.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Long countLikesByCommentId(Long commentId);
    Boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Long user(User user);
}
