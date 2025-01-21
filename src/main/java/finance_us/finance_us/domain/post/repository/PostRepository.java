package finance_us.finance_us.domain.post.repository;

import finance_us.finance_us.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value="SELECT * FROM post WHERE user_id=:userId;", nativeQuery=true)
    public List<Post> findByUserId(@Param("userId") Long userId);

    @Query(value="select p.* from post_like pl inner join post p on p.id=pl.post_id where pl.user_id=:userId;", nativeQuery=true)
    public List<Post> findByUserLiked(@Param("userId") Long userId);

}
