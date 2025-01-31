package finance_us.finance_us.domain.post.repository;

import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.status.PostType;
import org.springframework.data.domain.Pageable;
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

    @Query(value="select DISTINCT p.* from comment c inner join post p on p.id=c.post_id where c.user_id=:userId ;", nativeQuery=true)
    public List<Post> findByUserCommented(@Param("userId") Long userId);

    @Query(value="select p.* from post_scrap ps inner join post p on p.id=ps.post_id where ps.user_id=:userId ;", nativeQuery=true)
    public List<Post> findByUserScraped(@Param("userId") Long userId);

    //게시판 유형, 키워드를 통한 조회
    @Query("SELECT p FROM Post p WHERE p.postType = :boardType AND p.id > :lastId And (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY p.id ASC")
    List<Post> findByCategoryAndKeywordWithPaging(
            @Param("boardType") PostType boardType,
            @Param("lastId") Long lastId,
            @Param("keyword") String keyword,
            Pageable pageable);

}
