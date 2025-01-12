package finance_us.finance_us.domain.account.dto;

import lombok.*;

public class AccountResponse {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountResponseDTO{
        private Long accountId;
    }
}
