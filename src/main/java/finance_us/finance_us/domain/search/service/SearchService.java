package finance_us.finance_us.domain.search.service;

import finance_us.finance_us.domain.follows.repository.FollowRepository;
import finance_us.finance_us.domain.follows.service.FollowService;
import finance_us.finance_us.domain.post.entity.status.PostType;
import finance_us.finance_us.domain.search.dto.PostResponse;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.search.dto.PostSearchResponse;
import finance_us.finance_us.domain.search.dto.UserResponse;
import finance_us.finance_us.domain.search.dto.UserSearchResponse;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowService followService;
    private final FollowRepository followRepository;
    private final TokenProvider tokenProvider;

    public PostSearchResponse searchPosts(
            PostType boardType, Long lastId, String keyword, int size){
        lastId = (lastId == null) ? 0 : lastId;

        PageRequest pageRequest = PageRequest.of(0, size);

        List<Post> posts = postRepository.findByCategoryAndKeywordWithPaging(boardType, lastId, keyword, pageRequest);

        List<PostResponse> postDtos = posts.stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getUser().getName(),
                        post.getPostType().toString()))
                .collect(Collectors.toList());

        Long newLastId = posts.isEmpty() ? null : posts.get(posts.size() - 1).getId();

        return new PostSearchResponse(postDtos, newLastId);

    }

    public UserSearchResponse searchUsers(
            String token, String keyword, Long lastId, int size){

        Long userId = tokenProvider.extractUserIdFromToken(token);
        lastId = (lastId == null) ? 0 : lastId;
        PageRequest pageRequest = PageRequest.of(0, size);

        List<User> users = userRepository.findByNameContainingWithPagingAndExcludeSelf(keyword, lastId, userId, pageRequest);

        List<UserResponse> userDtos = users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        followRepository.existsByUserIdAndFollowingId(userId, user.getId())))
                .collect(Collectors.toList());

        Long newLastId = users.isEmpty() ? null : users.get(users.size() - 1).getId();

        return new UserSearchResponse(userDtos, newLastId);
    }

}
