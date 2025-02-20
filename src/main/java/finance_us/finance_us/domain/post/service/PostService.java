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
import finance_us.finance_us.domain.post.repository.PostScrapRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.security.TokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final PostScrapRepository postScrapRepository;

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
                .imageName(request.getImageName())
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
        post.setImageName(request.getImageName());

        return postRepository.save(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(String token, Long postId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        // 작성자 검증
        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this post.");
        }
        
        // 1.삭제하려는 게시글의 좋아요 데이터 삭제
        postLikeRepository.deleteByPost(post);
        
        // 2.게시글 삭제
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

    // 특정 게시판의 게시글 목록 조회
    public PostResponse.PostListByPostTypeDTO getPostsByPostType(String token, PostType postType, Long cursor, int size) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Post> posts = postRepository.findPostsByPostTypeWithCursor(postType, cursor, pageable);
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        if (postIds.isEmpty()) {
            return new PostResponse.PostListByPostTypeDTO(postType, List.of(), null);
        }

        Map<Long, Long> likeCountMap = postRepository.countLikesByPostIds(postIds)
                .stream().collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Long) result[1]
                ));
        Map<Long, Long> commentCountMap = postRepository.countCommentsByPostIds(postIds)
                .stream().collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Long) result[1]
                ));

        List<PostResponse.PostListByPostTypeResponse> postListByPostTypeResponses = posts.stream()
                .map(post -> new PostResponse.PostListByPostTypeResponse(
                        post,
                        likeCountMap.getOrDefault(post.getId(), 0L),
                        commentCountMap.getOrDefault(post.getId(), 0L)
                )).toList();

        Long nextCursor = posts.size() < size ? null : posts.get(posts.size() - 1).getId();

        return new PostResponse.PostListByPostTypeDTO(postType, postListByPostTypeResponses, nextCursor);
    }

    // 특정 게시판의 특정 카테고리 목록 조회
    public PostResponse.PostListByCategoryDTO getPostsByCategory(String token, PostType postType, Category category, Long cursor, int size) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        if (!isValidCategoryForPostType(postType, category)) {
            throw new GeneralException(ErrorStatus.CATEGORY_NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Post> posts = postRepository.findPostsByCategoryWithCursor(postType, category, cursor, pageable);
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        if (postIds.isEmpty()) {
            return new PostResponse.PostListByCategoryDTO(postType, category, List.of(), null);
        }

        Map<Long, Long> likeCountMap = postRepository.countLikesByPostIds(postIds)
                .stream().collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Long) result[1]
                ));
        Map<Long, Long> commentCountMap = postRepository.countCommentsByPostIds(postIds)
                .stream().collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Long) result[1]
                ));

        List<PostResponse.PostListByCategoryResponse> postListByCategoryResponses = posts.stream()
                .map(post -> new PostResponse.PostListByCategoryResponse(
                        post,
                        likeCountMap.getOrDefault(post.getId(), 0L),
                        commentCountMap.getOrDefault(post.getId(), 0L)
                )).toList();

        Long nextCursor = posts.size() < size ? null : posts.get(posts.size() - 1).getId();

        return new PostResponse.PostListByCategoryDTO(postType, category, postListByCategoryResponses, nextCursor);
    }

    // 특정 게시글 조회
    public PostResponse.PostByBoardDTO getPost(String token, Long postId) {
        Long userId = tokenProvider.extractUserIdFromToken(token);

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isLiked = postRepository.existsByPostAndUser(post, user);

        boolean isMine = post.getUser().getId().equals(userId);

        boolean isScraped = postScrapRepository.existsByUserAndPost(user, post);

        return new PostResponse.PostByBoardDTO (
                post.getId(),
                post.getUser().getId(),
                post.getUser().getName(),
                post.getUser().getImage(), // 유저 프로필사진 url
                post.getUser().getImageName(),
                post.getUser().isAuthenticated(),
                isLiked,
                isMine,
                isScraped,
                post.getTitle(),
                post.getContent(),
                post.getPostType(),
                post.getCategory(),
                post.getImageUrl(), // 게시글 작성 시 첨부하는 사진 url
                post.getImageName(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    // postType 에 해당하는 유효한 category 인지 검증
    private boolean isValidCategoryForPostType(PostType postType, Category category) {
        return switch (postType) {
            case FREE -> List.of(Category.FREE, Category.INFO, Category.WASTE, Category.SAVE).contains(category);
            case INFO -> List.of(Category.COLUMN, Category.LECTURE, Category.PROMOTION).contains(category);
            default -> false;
        };
    }
}
