package finance_us.finance_us.domain.post.repository;

import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.PostLike;
import finance_us.finance_us.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Long countLikesByPostId(Long postId);
    Boolean existsByPostAndUser(Post post, User user);
}
