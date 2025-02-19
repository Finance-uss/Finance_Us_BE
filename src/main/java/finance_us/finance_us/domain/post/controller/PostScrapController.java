package finance_us.finance_us.domain.post.controller;

import finance_us.finance_us.domain.post.dto.PostScrapResponse;
import finance_us.finance_us.domain.post.service.PostScrapService;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scrap/{postId}")
@Tag(name = "Post-Scrap API", description = "게시글 스크랩 관련 API")
public class PostScrapController {
    private final PostScrapService postScrapService;
    
    // 게시글 스크랩 추가 및 삭제
    @PostMapping
    @Operation(summary = "게시글 스크랩 추가 및 삭제 API")
    public ApiResponse<PostScrapResponse.PostScrapResponseDTO> scrapPost(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        PostScrapResponse.PostScrapResponseDTO scrapResponseDTO = postScrapService.scrapPost(token, postId);

        return ApiResponse.onSuccess(scrapResponseDTO);
    }
}
