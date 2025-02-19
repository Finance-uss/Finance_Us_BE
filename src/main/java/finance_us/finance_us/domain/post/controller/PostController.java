package finance_us.finance_us.domain.post.controller;

import finance_us.finance_us.domain.post.converter.PostConverter;
import finance_us.finance_us.domain.post.dto.PostRequest;
import finance_us.finance_us.domain.post.dto.PostResponse;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.status.Category;
import finance_us.finance_us.domain.post.entity.status.PostType;
import finance_us.finance_us.domain.post.service.PostService;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
@Tag(name = "Post API", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping
    @Operation(summary = "게시글 작성 API")
    public ApiResponse<PostResponse.PostResponseDTO> createPost(@RequestHeader("Authorization") String token, @RequestBody PostRequest.PostRequestDTO request) {
        Post post = postService.createPost(token, request);
        return ApiResponse.onSuccess(PostConverter.toPostResponseDTO(post));
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    @Operation(summary = "게시글 수정 API")
    public ApiResponse<PostResponse.PostResponseDTO> updatePost(@RequestHeader("Authorization") String token, @PathVariable Long postId, @RequestBody PostRequest.PostRequestDTO request) {
        Post post = postService.updatePost(token, postId, request);
        return ApiResponse.onSuccess(PostConverter.toPostResponseDTO(post));
    }
    
    // 게시글 삭제
    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제 API")
    public ApiResponse<Boolean> deletePost(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        postService.deletePost(token, postId);
        return ApiResponse.onSuccess(true);
    }

    // 특정 게시글 조회
    @GetMapping("/detail/{postId}")
    @Operation(summary = "특정 게시글 조회 API")
    public ApiResponse<PostResponse.PostByBoardDTO> getPost(@RequestHeader("Authorization") String token, @PathVariable("postId") Long postId) {
        return ApiResponse.onSuccess(postService.getPost(token, postId));
    }

    @GetMapping("/posted-post/{userId}")
    public ApiResponse<?> getPostedPostList(@PathVariable("userId") Long userId)
    {

        return ApiResponse.onSuccess(postService.getPostedPostList(userId));
    }

    @GetMapping("/liked-post/{userId}")
    public ApiResponse<?> getLikedPostList(@PathVariable("userId") Long userId)
    {

        return ApiResponse.onSuccess(postService.getLikedPostList(userId));
    }

    @GetMapping("/commented-post/{userId}")
    public ApiResponse<?> getCommentedPostList(@PathVariable("userId") Long userId)
    {

        return ApiResponse.onSuccess(postService.getCommentedPostList(userId));
    }

    @GetMapping("/scraped-post/{userId}")
    public ApiResponse<?> getScrapedPostList(@PathVariable("userId") Long userId)
    {

        return ApiResponse.onSuccess(postService.getScrapedPostList(userId));
    }

    // 특정 게시판 목록 조회
    @GetMapping("/{postType}")
    @Operation(summary = "특정 게시판 목록 조회 API")
    public ApiResponse<PostResponse.PostListByPostTypeDTO> getPostsByPostType(@RequestHeader("Authorization") String token, @PathVariable("postType") PostType postType, @RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "3") int size)
    {
        return ApiResponse.onSuccess(postService.getPostsByPostType(token, postType, cursor, size));
    }

    // 특정 게시판의 특정 카테고리 목록 조회
    @GetMapping("/{postType}/{category}")
    @Operation(summary = "특정 게시판의 특정 카테고리 목록 조회 API")
    public ApiResponse<PostResponse.PostListByCategoryDTO> getPostsByCategory(@RequestHeader("Authorization") String token, @PathVariable("postType") PostType postType, @PathVariable Category category, @RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "3") int size)
    {
        return ApiResponse.onSuccess(postService.getPostsByCategory(token, postType, category, cursor, size));
    }
}