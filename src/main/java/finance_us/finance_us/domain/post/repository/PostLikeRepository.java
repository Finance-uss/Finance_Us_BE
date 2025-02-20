package finance_us.finance_us.domain.post.repository;

import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Long countLikesByPostId(Long postId);
    Boolean existsByPostIdAndUserId(Long postId, Long userId);
    void deleteByPost(Post post);
}
