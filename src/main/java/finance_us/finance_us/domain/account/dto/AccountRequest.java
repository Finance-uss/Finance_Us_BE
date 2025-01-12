package finance_us.finance_us.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class AccountRequest {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountRequestDTO{
        private String accountType;  // expense, income
        private LocalDate date;
        private String subName;
        private String subAssetName;
        private Long amount;
        private String title;
        private Boolean status;
        private Integer score;
        private String imageUrl;
        private String content;
    }

}
