package finance_us.finance_us.domain.account.dto;

import lombok.*;

public class CheerResponse {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheerResponseDTO{
        private Long accountId;
        private int totalCheer;
    }
}
