package finance_us.finance_us.domain.follows.controller;

import finance_us.finance_us.domain.follows.dto.FollowResponse;
import finance_us.finance_us.domain.follows.dto.FollowResponseWithLastId;
import finance_us.finance_us.domain.follows.entity.Follow;
import finance_us.finance_us.domain.follows.service.FollowService;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{followingId}")
    @Operation(summary = "팔로우 추가 API")
    public ApiResponse<Void> addFollow(
            @RequestHeader("Authorization") String token,
            @PathVariable Long followingId
    ) {
        followService.addFollow(token, followingId);
        return ApiResponse.onSuccess(null);
    }

    @DeleteMapping("/{followingId}")
    @Operation(summary = "팔로우 취소 API")
    public ApiResponse<Void> removeFollow(
            @RequestHeader("Authorization") String token,
            @PathVariable Long followingId
    ) {
        followService.removeFollow(token, followingId);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping
    @Operation(summary = "팔로우 목록 조회(스크롤 포함) API")
    public ApiResponse<FollowResponseWithLastId> getFollows(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Long lastFollowingId,
            @RequestParam(defaultValue = "10") int size
    ) {
        FollowResponseWithLastId follows = followService.getFollows(token, lastFollowingId, size);
        return ApiResponse.onSuccess(follows);
    }
}
