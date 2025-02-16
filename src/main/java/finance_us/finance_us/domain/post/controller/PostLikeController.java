package finance_us.finance_us.domain.post.controller;

import finance_us.finance_us.domain.post.dto.PostLikeResponse;
import finance_us.finance_us.domain.post.service.PostLikeService;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like/post/{postId}")
@Tag(name = "Post-Like API", description = "게시글 좋아요 관련 API")
public class PostLikeController {
    private final PostLikeService postLikeService;
    
    // 게시글 좋아요 추가
    @PostMapping
    @Operation(summary = "게시글 좋아요 추가 API")
    public ApiResponse<PostLikeResponse.PostLikeResponseDTO> likePost(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        PostLikeResponse.PostLikeResponseDTO likeResponseDTO = postLikeService.likePost(token, postId);

        return ApiResponse.onSuccess(likeResponseDTO);
    }

    // 게시글 좋아요 갯수 반환
    @GetMapping
    @Operation(summary = "게시글 좋아요 갯수 조회 API")
    public ApiResponse<PostLikeResponse.PostLikeResponseDTO> getPostLikes(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        PostLikeResponse.PostLikeResponseDTO postLikeResponseDTO = postLikeService.getPostLikes(token, postId);

        return ApiResponse.onSuccess(postLikeResponseDTO);
    }
}
