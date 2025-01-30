package finance_us.finance_us.domain.post.service;

import finance_us.finance_us.domain.post.converter.PostScrapConverter;
import finance_us.finance_us.domain.post.dto.PostScrapResponse;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.PostScrap;
import finance_us.finance_us.domain.post.repository.PostRepository;
import finance_us.finance_us.domain.post.repository.PostScrapRepository;
import finance_us.finance_us.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;

    @Transactional
    // 게시글 스크랩 추가 및 삭제
    public PostScrapResponse.PostScrapResponseDTO scrapPost(Long postId, User user) {
        // 게시글 유효성 검증
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("Post not found"));

        Optional<PostScrap> existingScrap = postScrapRepository.findByPostAndUser(post, user);

        if (existingScrap.isPresent()) {
            // 스크랩 삭제
            postScrapRepository.deleteByPostAndUser(post, user);
            return PostScrapConverter.toPostScrapResponseDTO(existingScrap.get(), false);
        } else {
            // 스크랩 추가
            PostScrap postScrap = PostScrap.builder()
                    .post(post)
                    .user(user)
                    .build();

            postScrapRepository.save(postScrap);
            return PostScrapConverter.toPostScrapResponseDTO(postScrap, true);
        }
    }
}
