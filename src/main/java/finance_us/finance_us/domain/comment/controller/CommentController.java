package finance_us.finance_us.domain.comment.controller;

import finance_us.finance_us.domain.comment.converter.CommentConverter;
import finance_us.finance_us.domain.comment.dto.CommentRequest;
import finance_us.finance_us.domain.comment.dto.CommentResponse;
import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.service.CommentService;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "Comment API", description = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}")
    @Operation(summary = "댓글 작성 API")
    public ApiResponse<CommentResponse.CommentResponseDTO> createComment(@RequestHeader("Authorization") String token, @PathVariable Long postId, @RequestBody CommentRequest.CommentRequestDTO request) {
        Comment comment = commentService.createComment(token, postId, request);
        return ApiResponse.onSuccess(CommentConverter.toCommentResponseDTO(comment));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정 API")
    public ApiResponse<CommentResponse.CommentResponseDTO> updateComment(@RequestHeader("Authorization") String token, @PathVariable Long commentId, @RequestBody CommentRequest.CommentUpdateDTO request) {
        Comment comment = commentService.updateComment(token, commentId, request);
        return ApiResponse.onSuccess(CommentConverter.toCommentResponseDTO(comment));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제 API")
    public ApiResponse<Boolean> deleteComment(@RequestHeader("Authorization") String token, @PathVariable Long commentId) {
        commentService.deleteComment(token, commentId);
        return ApiResponse.onSuccess(true);
    }

    // 댓글 갯수 및 목록 반환
    @GetMapping("/{postId}")
    @Operation(summary = "댓글 갯수 및 목록 조회 API")
    public ApiResponse<CommentResponse.CommentResultDTO> getCommentsByPost(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        List<CommentResponse.CommentDTO> commentsList = commentService.getCommentsByPostWithReplies(token, postId);
        int commentCount = commentService.getCommentCount(token, postId);

        CommentResponse.CommentResultDTO commentResultDTO = CommentResponse.CommentResultDTO.builder()
                .postId(postId)
                .commentCount(commentCount)
                .commentsList(commentsList)
                .build();

        return ApiResponse.onSuccess(commentResultDTO);
    }
}
