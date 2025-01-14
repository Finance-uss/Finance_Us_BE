package finance_us.finance_us.domain.post.controller;

import finance_us.finance_us.domain.post.converter.PostConverter;
import finance_us.finance_us.domain.post.dto.PostRequest;
import finance_us.finance_us.domain.post.dto.PostResponse;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.service.PostService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping
    public ApiResponse<PostResponse.PostResponseDTO> createPost(@RequestBody PostRequest.PostRequestDTO request) {
        Post post = postService.createPost(request);
        return ApiResponse.onSuccess(PostConverter.toPostResponseDTO(post));
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ApiResponse<PostResponse.PostResponseDTO> updatePost(@PathVariable Long postId, @RequestBody PostRequest.PostRequestDTO request) {
        Post post = postService.updatePost(postId, request);
        return ApiResponse.onSuccess(PostConverter.toPostResponseDTO(post));
    }
    
    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ApiResponse<Boolean> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ApiResponse.onSuccess(true);
    }
}