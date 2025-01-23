package finance_us.finance_us.domain.post.service;

import finance_us.finance_us.domain.post.dto.PostRequest;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.status.Category;
import finance_us.finance_us.domain.post.entity.status.PostType;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 생성
    public Post createPost(PostRequest.PostRequestDTO request) {
        // 사용자 유효성 검증
//        User user = userRepository.findById(userId)
//                .orElseThrow(()->new IllegalArgumentException("User not found"));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postType(PostType.valueOf(request.getPostType()))
                .category(Category.valueOf(request.getCategory()))
                .imageUrl(request.getImageUrl())
                //.user(user)
                .build();

        return postRepository.save(post);
    }

    // 게시글 수정
    public Post updatePost(Long postId, PostRequest.PostRequestDTO request) {
        Post post = postRepository.findById(postId)
                        .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPostType(PostType.valueOf(request.getPostType()));
        post.setCategory(Category.valueOf(request.getCategory()));
        post.setImageUrl(request.getImageUrl());

        return postRepository.save(post);
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        postRepository.delete(post);
    }
}
