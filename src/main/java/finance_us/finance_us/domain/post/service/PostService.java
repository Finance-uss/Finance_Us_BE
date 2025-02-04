package finance_us.finance_us.domain.post.service;

import finance_us.finance_us.domain.comment.repository.CommentRepository;
import finance_us.finance_us.domain.post.converter.PostConverter;
import finance_us.finance_us.domain.post.dto.PostRequest;
import finance_us.finance_us.domain.post.dto.PostResponse;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.status.Category;
import finance_us.finance_us.domain.post.entity.status.PostType;
import finance_us.finance_us.domain.post.repository.PostLikeRepository;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.security.TokenProvider;
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
    private final TokenProvider tokenProvider;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    // 게시글 생성
    public Post createPost(String token, PostRequest.PostRequestDTO request) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // 사용자 유효성 검증
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("User not found"));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postType(PostType.valueOf(request.getPostType()))
                .category(Category.valueOf(request.getCategory()))
                .imageUrl(request.getImageUrl())
                .user(user)
                .build();

        return postRepository.save(post);
    }

    // 게시글 수정
    public Post updatePost(String token, Long postId, PostRequest.PostRequestDTO request) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Post post = postRepository.findById(postId)
                        .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        // 작성자 검증
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to update this post.");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPostType(PostType.valueOf(request.getPostType()));
        post.setCategory(Category.valueOf(request.getCategory()));
        post.setImageUrl(request.getImageUrl());

        return postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(String token, Long postId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        // 작성자 검증
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this post.");
        }

        postRepository.delete(post);
    }

    // 유저가 게시한 게시물 조회
    public List<PostResponse.PostListDto> getPostedPostList(Long userId) {

        var postList = postRepository.findByUserLiked(userId);
        var dtoList = postList.stream().map(p -> {
            var likeCnt = postLikeRepository.countLikesByPostId(p.getId());
            var commentCnt = commentRepository.countByPostId(p.getId());
            return PostConverter.toPostListDto(p, likeCnt, commentCnt);

        }).toList();

        return dtoList;
    }
    // 유저가 좋아요 누른 게시물 조회
    public List<PostResponse.PostListDto> getLikedPostList(Long userId) {

        var postList = postRepository.findByUserLiked(userId);
        var dtoList = postList.stream().map(p -> {
            var likeCnt = postLikeRepository.countLikesByPostId(p.getId());
            var commentCnt = commentRepository.countByPostId(p.getId());
            return PostConverter.toPostListDto(p, likeCnt, commentCnt);

        }).toList();

        return dtoList;
    }

    // 유저가 댓글을 단 게시물 조회
    public List<PostResponse.PostListDto> getCommentedPostList(Long userId) {

        var postList = postRepository.findByUserLiked(userId);
        var dtoList = postList.stream().map(p -> {
            var likeCnt = postLikeRepository.countLikesByPostId(p.getId());
            var commentCnt = commentRepository.countByPostId(p.getId());
            return PostConverter.toPostListDto(p, likeCnt, commentCnt);

        }).toList();

        return dtoList;
    }

    // 유저가 댓글을 단 게시물 조회
    public List<PostResponse.PostListDto> getScrapedPostList(Long userId) {

        var postList = postRepository.findByUserLiked(userId);
        var dtoList = postList.stream().map(p -> {
            var likeCnt = postLikeRepository.countLikesByPostId(p.getId());
            var commentCnt = commentRepository.countByPostId(p.getId());
            return PostConverter.toPostListDto(p, likeCnt, commentCnt);

        }).toList();

        return dtoList;
    }




}
