package finance_us.finance_us.domain.post.controller;

import finance_us.finance_us.domain.comment.service.CommentService;
import finance_us.finance_us.domain.post.dto.PostLikeResponse;
import finance_us.finance_us.domain.post.entity.PostLike;
import finance_us.finance_us.domain.post.service.PostLikeService;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class PostLikeController {
    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    public ApiResponse<PostLikeResponse.PostLikeResponseDTO> likePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        PostLikeResponse.PostLikeResponseDTO likeResponseDTO = postLikeService.likePost(postId, user);

        return ApiResponse.onSuccess(likeResponseDTO);
    }
}
