package finance_us.finance_us.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import finance_us.finance_us.domain.post.entity.Post;
import finance_us.finance_us.domain.post.entity.status.Category;
import finance_us.finance_us.domain.post.entity.status.PostType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResponseDTO{
        private Long postId;
        private LocalDateTime createdAt;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime updatedAt;
    }

    @Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PostListDto{
        // 게시물 목록을 반환할때 사용합니다.

        String category;
        String imgUrl;
        String title;
        String content;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListByBoardDTO{
        // 커뮤니티에서 특정 게시판 목록 조회

        private PostType postType;
        private List<PostListByBoardResponse> posts;
        private Long nextCursor;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListByCategoryDTO{
        // 커뮤니티에서 특정 게시판의 특정 카테고리 목록 조회

        private PostType postType;
        private Category category;
        private List<PostListByBoardResponse> posts;
        private Long nextCursor;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListByBoardResponse {
        private Long postId;
        private String title;
        private String content;
        private String imageUrl;
        private Long likes;
        private Long comments;

        public PostListByBoardResponse(Post post, Long likeCount, Long commentCount) {
            this.postId = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.imageUrl = post.getImageUrl();
            this.likes = likeCount;
            this.comments = commentCount;
        }
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostByBoardDTO{
        private Long postId;
        private Long userId;
        private String name;
//        private String userImageUrl;
        private String title;
        private String content;
        private PostType postType;
        private Category category;
        private String imageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("createdAt")
        private LocalDateTime getCreatedAt() {
            return createdAt.isEqual(updatedAt) ? createdAt : null;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("updatedAt")
        private LocalDateTime getUpdatedAt() {
            return !createdAt.isEqual(updatedAt) ? updatedAt : null;
        }
    }
}
