package finance_us.finance_us.domain.account.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class AccountResponse {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountResponseDTO{
        private Long accountId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountImageResponseDTO{
        private String title;
        private Long amout;
        private LocalDate date;
        private List<String> content;
    }
}
