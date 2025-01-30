package finance_us.finance_us.domain.search.controller;

import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.status.PostType;
import finance_us.finance_us.domain.search.dto.PostSearchResponse;
import finance_us.finance_us.domain.search.dto.UserSearchResponse;
import finance_us.finance_us.domain.search.service.SearchService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/posts")
    public ApiResponse<PostSearchResponse> searchPosts(
            @RequestParam PostType boardType,
            @RequestParam(required = false) Long lastId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int size)
    {
        PostSearchResponse response = searchService.searchPosts(boardType, lastId, keyword, size);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/users")
    public ApiResponse<UserSearchResponse> searchUsers(
            @RequestParam String keyword,
            @RequestParam(required = false) Long lastId,
            @RequestParam Long currentUserId,
            @RequestParam(defaultValue = "10") int size)
    {

        UserSearchResponse response = searchService.searchUsers(keyword, lastId, currentUserId, size);
        return ApiResponse.onSuccess(response);
    }
}
