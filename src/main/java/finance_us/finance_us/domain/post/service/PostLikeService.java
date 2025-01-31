package finance_us.finance_us.domain.post.service;

import finance_us.finance_us.domain.post.converter.PostLikeConverter;
import finance_us.finance_us.domain.post.dto.PostLikeResponse;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.PostLike;
import finance_us.finance_us.domain.post.repository.PostLikeRepository;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    // 게시글에 좋아요 추가
    public PostLikeResponse.PostLikeResponseDTO likePost(Long postId, User user) {
        // 게시글 유효성 검증
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        // 이미 좋아요가 눌렸는지 확인
        if (postLikeRepository.existsByPostAndUser(post, user)) {
            throw new IllegalArgumentException("You already liked this post");
        }
    
        // 좋아요 저장
        PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(postLike);

        Long likesCount = postLikeRepository.countLikesByPostId(postId);

        return PostLikeConverter.toPostLikeResponseDTO(postLike, likesCount);
    }

    // 게시글 좋아요 갯수 반환
    public PostLikeResponse.PostLikeResponseDTO getPostLikes(Long postId) {
        // 게시글 유효성 검증
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        Long likesCount = postLikeRepository.countLikesByPostId(postId);

        return PostLikeConverter.toGetPostLikesResponseDTO(postId, likesCount);
    }
}
