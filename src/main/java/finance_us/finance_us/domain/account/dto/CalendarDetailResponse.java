package finance_us.finance_us.domain.account.dto;

import finance_us.finance_us.domain.account.entity.status.AccountType;
import lombok.*;

import java.time.LocalDate;

public class CalendarDetailResponse {

    // 가계부 달력 일별 조회
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalendarDetailResponseDTO {
        private Long accountId;
        private AccountType accountType;
        private LocalDate date;
        private String subName;
        private String subAssetName;
        private Long amount;
        private String title;
        private int score;
        private String imageUrl;
        private String imageName;
        private String content;
    }
}
