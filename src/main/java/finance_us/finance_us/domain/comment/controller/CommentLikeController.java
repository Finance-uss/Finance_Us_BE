package finance_us.finance_us.domain.comment.controller;

import finance_us.finance_us.domain.comment.dto.CommentLikeResponse;
import finance_us.finance_us.domain.comment.service.CommentLikeService;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like/comment/{commentId}")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @PostMapping
    public ApiResponse<CommentLikeResponse.CommentLikeResponseDTO> likeComment(@PathVariable Long commentId, @AuthenticationPrincipal User user) {
        CommentLikeResponse.CommentLikeResponseDTO likeResponseDTO = commentLikeService.likeComment(commentId, user);

        return ApiResponse.onSuccess(likeResponseDTO);
    }

    @GetMapping
    public ApiResponse<CommentLikeResponse.CommentLikeResponseDTO> getCommentLikes(@PathVariable Long commentId) {
        CommentLikeResponse.CommentLikeResponseDTO commentLikeResponseDTO = commentLikeService.getCommentLikes(commentId);

        return ApiResponse.onSuccess(commentLikeResponseDTO);
    }
}
