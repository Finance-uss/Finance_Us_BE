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

        Long postId;

        String category;
        String imgUrl;
        String title;
        String content;

        Long likeCnt;
        int commentCnt;

    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListByPostTypeDTO{
        // 커뮤니티에서 특정 게시판 목록 조회

        private PostType postType;
        private List<PostListByPostTypeResponse> posts;
        private Long nextCursor;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListByPostTypeResponse {
        private Long postId;
        private String title;
        private String content;
        private String imageUrl;
        private String imageName;
        private Long likes;
        private Long comments;
        private Category category;

        public PostListByPostTypeResponse(Post post, Long likeCount, Long commentCount) {
            this.postId = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.imageUrl = post.getImageUrl();
            this.imageName = post.getImageName();
            this.likes = likeCount;
            this.comments = commentCount;
            this.category = post.getCategory();
        }
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
        private List<PostListByCategoryResponse> posts;
        private Long nextCursor;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListByCategoryResponse {
        private Long postId;
        private String title;
        private String content;
        private String imageUrl;
        private String imageName;
        private Long likes;
        private Long comments;

        public PostListByCategoryResponse(Post post, Long likeCount, Long commentCount) {
            this.postId = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.imageUrl = post.getImageUrl();
            this.imageName = post.getImageName();
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
        // 특정 게시글 조회

        private Long postId;
        private Long userId;
        private String name;
        private String userImageUrl;
        private String userImageName;
        private Boolean isAuthenticated;
        private Boolean isLiked;
        private Boolean isMine;
        private String title;
        private String content;
        private PostType postType;
        private Category category;
        private String imageUrl;
        private String imageName;
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
