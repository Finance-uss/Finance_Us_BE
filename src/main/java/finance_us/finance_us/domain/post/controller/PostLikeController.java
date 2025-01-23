package finance_us.finance_us.domain.post.controller;

import finance_us.finance_us.domain.post.dto.PostLikeResponse;
import finance_us.finance_us.domain.post.service.PostLikeService;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like/post/{postId}")
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping
    public ApiResponse<PostLikeResponse.PostLikeResponseDTO> likePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        PostLikeResponse.PostLikeResponseDTO likeResponseDTO = postLikeService.likePost(postId, user);

        return ApiResponse.onSuccess(likeResponseDTO);
    }

    @GetMapping
    public ApiResponse<PostLikeResponse.PostLikeResponseDTO> getPostLikes(@PathVariable Long postId) {
        PostLikeResponse.PostLikeResponseDTO postLikeResponseDTO = postLikeService.getPostLikes(postId);

        return ApiResponse.onSuccess(postLikeResponseDTO);
    }
}
