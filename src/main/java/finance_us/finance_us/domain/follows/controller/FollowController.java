package finance_us.finance_us.domain.follows.controller;

import finance_us.finance_us.domain.follows.dto.FollowResponse;
import finance_us.finance_us.domain.follows.dto.FollowResponseWithLastId;
import finance_us.finance_us.domain.follows.entity.Follow;
import finance_us.finance_us.domain.follows.service.FollowService;
import finance_us.finance_us.global.ApiResponse;
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
    public ApiResponse<Void> addFollow(
            @PathVariable Long followingId,
            @RequestParam Long userId
            //@RequestHeader("Authorization") String token
    ) {
        followService.addFollow(userId, followingId);
        return ApiResponse.onSuccess(null);
    }

    @DeleteMapping("/{followingId}")
    public ApiResponse<Void> removeFollow(
            @PathVariable Long followingId,
            @RequestParam Long userId
            //@RequestHeader("Authorization") String token
    ) {
        followService.removeFollow(userId, followingId);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping
    public ApiResponse<FollowResponseWithLastId> getFollows(
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastfollowingId,
            @RequestParam(defaultValue = "10") int size
            //@RequestHeader("Authorization") String token
    ) {
        FollowResponseWithLastId follows = followService.getFollows(userId, lastfollowingId, size);
        return ApiResponse.onSuccess(follows);
    }
}
