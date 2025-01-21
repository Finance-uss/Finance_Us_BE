package finance_us.finance_us.domain.comment.controller;

import finance_us.finance_us.domain.comment.converter.CommentConverter;
import finance_us.finance_us.domain.comment.dto.CommentRequest;
import finance_us.finance_us.domain.comment.dto.CommentResponse;
import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.service.CommentService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}")
    public ApiResponse<CommentResponse.CommentResponseDTO> createComment(@PathVariable Long postId, @RequestBody CommentRequest.CommentRequestDTO request) {
        Comment comment = commentService.createComment(postId, request);
        return ApiResponse.onSuccess(CommentConverter.toCommentResponseDTO(comment));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ApiResponse<CommentResponse.CommentResponseDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest.CommentRequestDTO request) {
        Comment comment = commentService.updateComment(commentId, request);
        return ApiResponse.onSuccess(CommentConverter.toCommentResponseDTO(comment));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ApiResponse<Boolean> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.onSuccess(true);
    }
}
