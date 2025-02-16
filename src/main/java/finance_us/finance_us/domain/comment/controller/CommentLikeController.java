package finance_us.finance_us.domain.comment.controller;

import finance_us.finance_us.domain.comment.dto.CommentLikeResponse;
import finance_us.finance_us.domain.comment.service.CommentLikeService;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like/comment/{commentId}")
@Tag(name = "Comment-Like API", description = "댓글 좋아요 관련 API")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    // 댓글 좋아요 추가
    @PostMapping
    @Operation(summary = "댓글 좋아요 추가 API")
    public ApiResponse<CommentLikeResponse.CommentLikeResponseDTO> likeComment(@RequestHeader("Authorization") String token, @PathVariable Long commentId) {
        CommentLikeResponse.CommentLikeResponseDTO likeResponseDTO = commentLikeService.likeComment(token, commentId);

        return ApiResponse.onSuccess(likeResponseDTO);
    }

    // 댓글 좋아요 갯수 반환
    @GetMapping
    @Operation(summary = "댓글 좋아요 갯수 조회 API")
    public ApiResponse<CommentLikeResponse.CommentLikeResponseDTO> getCommentLikes(@RequestHeader("Authorization") String token, @PathVariable Long commentId) {
        CommentLikeResponse.CommentLikeResponseDTO commentLikeResponseDTO = commentLikeService.getCommentLikes(token, commentId);

        return ApiResponse.onSuccess(commentLikeResponseDTO);
    }
}
