package finance_us.finance_us.domain.post.repository;

import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.status.Category;
import finance_us.finance_us.domain.post.entity.status.PostType;
import finance_us.finance_us.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT p.id, COUNT(pl) FROM Post p LEFT JOIN PostLike pl ON p.id = pl.post.id WHERE p.id IN :postIds GROUP BY p.id")
    List<Object[]> countLikesByPostIds(@Param("postIds") List<Long> postIds);

    @Query("SELECT p.id, COUNT(c) FROM Post p LEFT JOIN Comment c ON p.id = c.post.id WHERE p.id IN :postIds GROUP BY p.id")
    List<Object[]> countCommentsByPostIds(@Param("postIds") List<Long> postIds);

    @Query("""
    SELECT p FROM Post p 
    WHERE p.postType = :postType 
    AND (:cursor IS NULL OR p.id < :cursor) 
    ORDER BY p.id DESC
""")
    List<Post> findPostsByPostTypeWithCursor(
            @Param("postType") PostType postType,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Post p 
    WHERE p.postType = :postType
    AND p.category = :category
    AND (:cursor IS NULL OR p.id < :cursor) 
    ORDER BY p.id DESC 
""")
    List<Post> findPostsByCategoryWithCursor(
            @Param("postType") PostType postType,
            @Param("category") Category category,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
    
    // 특정 게시글 조회
    Optional<Post> findById(Long postId);

    @Query("SELECT COUNT(pl) > 0 FROM PostLike pl WHERE pl.post = :post AND pl.user = :user")
    boolean existsByPostAndUser(@Param("post") Post post, @Param("user") User user);
}
