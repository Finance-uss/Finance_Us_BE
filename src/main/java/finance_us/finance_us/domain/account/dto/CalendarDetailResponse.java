package finance_us.finance_us.domain.account.dto;

import lombok.*;

public class CalendarDetailResponse {

    // 가계부 달력 일별 조회
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CalendarDetailResponseDTO {
        private Long accountId;
        private int score;
        private String title;
        private Long amount;
        private String subName;
        private String imageUrl;
    }
}
