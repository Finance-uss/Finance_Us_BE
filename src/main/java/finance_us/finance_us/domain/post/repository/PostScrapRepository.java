package finance_us.finance_us.domain.post.repository;

import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.PostScrap;
import finance_us.finance_us.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    void deleteByPostAndUser(Post post, User user);
    Optional<PostScrap> findByPostAndUser(Post post, User user);
}
