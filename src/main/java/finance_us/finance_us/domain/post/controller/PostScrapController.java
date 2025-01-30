package finance_us.finance_us.domain.post.controller;

import finance_us.finance_us.domain.post.dto.PostScrapResponse;
import finance_us.finance_us.domain.post.service.PostScrapService;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scrap/{postId}")
public class PostScrapController {
    private final PostScrapService postScrapService;

    @PostMapping
    public ApiResponse<PostScrapResponse.PostScrapResponseDTO> scrapPost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        PostScrapResponse.PostScrapResponseDTO scrapResponseDTO = postScrapService.scrapPost(postId, user);

        return ApiResponse.onSuccess(scrapResponseDTO);
    }
}
