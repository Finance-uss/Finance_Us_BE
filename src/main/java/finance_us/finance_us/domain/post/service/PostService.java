package finance_us.finance_us.domain.post.service;

import finance_us.finance_us.domain.post.converter.PostConverter;
import finance_us.finance_us.domain.post.dto.PostRequest;
import finance_us.finance_us.domain.post.dto.PostResponse;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.status.Category;
import finance_us.finance_us.domain.post.entity.status.PostType;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 생성
    public Post createPost(PostRequest.PostRequestDTO request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postType(PostType.valueOf(request.getPostType()))
                .category(Category.valueOf(request.getCategory()))
                .imageUrl(request.getImageUrl())
                .createdAt(LocalDateTime.now())
                //.user(user)
                .build();

        return postRepository.save(post);
    }

    // 게시글 수정
    public Post updatePost(Long postId, PostRequest.PostRequestDTO request) {
        Post post = postRepository.findById(postId)
                        .orElseThrow(()-> new IllegalArgumentException("Post no found"));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPostType(PostType.valueOf(request.getPostType()));
        post.setCategory(Category.valueOf(request.getCategory()));
        post.setImageUrl(request.getImageUrl());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        postRepository.delete(post);
    }

    // 유저가 게시한 게시물 조회
    public List<PostResponse.PostListDto> getPostedPostList(Long userId) {

        var postList = postRepository.findByUserId(userId);
        var dtoList = postList.stream().map(PostConverter::toPostListDto).toList();

        return dtoList;
    }
    // 유저가 좋아요 누른 게시물 조회
    public List<PostResponse.PostListDto> getLikedPostList(Long userId) {

        var postList = postRepository.findByUserLiked(userId);
        var dtoList = postList.stream().map(PostConverter::toPostListDto).toList();

        return dtoList;
    }
}
