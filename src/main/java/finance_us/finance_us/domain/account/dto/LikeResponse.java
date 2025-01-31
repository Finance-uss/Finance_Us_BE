package finance_us.finance_us.domain.account.dto;

import lombok.*;


public class LikeResponse {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LikeResponseDTO{
        private Long accountId;
        private int totalLike;
    }
}