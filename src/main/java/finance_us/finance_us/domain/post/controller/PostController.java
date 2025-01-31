package finance_us.finance_us.domain.post.controller;

import finance_us.finance_us.domain.post.converter.PostConverter;
import finance_us.finance_us.domain.post.dto.PostRequest;
import finance_us.finance_us.domain.post.dto.PostResponse;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.service.PostService;
import finance_us.finance_us.global.ApiResponse;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping
    public ApiResponse<PostResponse.PostResponseDTO> createPost(@RequestHeader("Authorization") String token, @RequestBody PostRequest.PostRequestDTO request) {
        Post post = postService.createPost(token, request);
        return ApiResponse.onSuccess(PostConverter.toPostResponseDTO(post));
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ApiResponse<PostResponse.PostResponseDTO> updatePost(@RequestHeader("Authorization") String token, @PathVariable Long postId, @RequestBody PostRequest.PostRequestDTO request) {
        Post post = postService.updatePost(token, postId, request);
        return ApiResponse.onSuccess(PostConverter.toPostResponseDTO(post));
    }
    
    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ApiResponse<Boolean> deletePost(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        postService.deletePost(token, postId);
        return ApiResponse.onSuccess(true);
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


}