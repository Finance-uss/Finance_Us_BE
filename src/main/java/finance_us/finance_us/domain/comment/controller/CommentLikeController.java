package finance_us.finance_us.domain.comment.controller;

import finance_us.finance_us.domain.comment.dto.CommentLikeResponse;
import finance_us.finance_us.domain.comment.service.CommentLikeService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like/comment/{commentId}")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    // 댓글 좋아요 추가
    @PostMapping
    public ApiResponse<CommentLikeResponse.CommentLikeResponseDTO> likeComment(@RequestHeader("Authorization") String token, @PathVariable Long commentId) {
        CommentLikeResponse.CommentLikeResponseDTO likeResponseDTO = commentLikeService.likeComment(token, commentId);

        return ApiResponse.onSuccess(likeResponseDTO);
    }

    // 댓글 좋아요 갯수 반환
    @GetMapping
    public ApiResponse<CommentLikeResponse.CommentLikeResponseDTO> getCommentLikes(@PathVariable Long commentId) {
        CommentLikeResponse.CommentLikeResponseDTO commentLikeResponseDTO = commentLikeService.getCommentLikes(commentId);

        return ApiResponse.onSuccess(commentLikeResponseDTO);
    }
}
