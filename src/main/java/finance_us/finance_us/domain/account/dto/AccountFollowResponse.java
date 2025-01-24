package finance_us.finance_us.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountFollowResponse {
    private String name;
    private Object expenseRate;
    private List<AccountFollowResponseDTO> accounts;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountFollowResponseDTO {
        private Long accountId;
        private int score;
        private String title;
        private Long amount;
        private LocalDate date;
        private String subName;
        private String imageUrl;
        private int totalLike;
        private int totalCheer;
    }
}
