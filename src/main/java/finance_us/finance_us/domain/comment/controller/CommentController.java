package finance_us.finance_us.domain.comment.controller;

import finance_us.finance_us.domain.comment.converter.CommentConverter;
import finance_us.finance_us.domain.comment.dto.CommentRequest;
import finance_us.finance_us.domain.comment.dto.CommentResponse;
import finance_us.finance_us.domain.comment.entity.Comment;
import finance_us.finance_us.domain.comment.service.CommentService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}")
    public ApiResponse<CommentResponse.CommentResponseDTO> createComment(@RequestHeader("Authorization") String token, @PathVariable Long postId, @RequestBody CommentRequest.CommentRequestDTO request) {
        Comment comment = commentService.createComment(token, postId, request);
        return ApiResponse.onSuccess(CommentConverter.toCommentResponseDTO(comment));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ApiResponse<CommentResponse.CommentResponseDTO> updateComment(@RequestHeader("Authorization") String token, @PathVariable Long commentId, @RequestBody CommentRequest.CommentRequestDTO request) {
        Comment comment = commentService.updateComment(token, commentId, request);
        return ApiResponse.onSuccess(CommentConverter.toCommentResponseDTO(comment));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ApiResponse<Boolean> deleteComment(@RequestHeader("Authorization") String token, @PathVariable Long commentId) {
        commentService.deleteComment(token, commentId);
        return ApiResponse.onSuccess(true);
    }

    // 댓글 갯수 및 목록 반환
    @GetMapping("/{postId}")
    public ApiResponse<CommentResponse.CommentResultDTO> getCommentsByPost(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        List<Comment> commentsList = commentService.getCommentsByPost(token, postId);
        int commentCount = commentService.getCommentCount(token, postId);

        List<CommentResponse.CommentDTO> commentDTOS = commentsList.stream()
                .map(CommentConverter::toCommentDTO)
                .collect(Collectors.toList());

        CommentResponse.CommentResultDTO commentResultDTO = CommentResponse.CommentResultDTO.builder()
                .postId(postId)
                .commentCount(commentCount)
                .commentsList(commentDTOS)
                .build();

        return ApiResponse.onSuccess(commentResultDTO);
    }
}
