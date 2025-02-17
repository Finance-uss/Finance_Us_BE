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
public class FollowResponse {
    private String name;
    private Object expenseRate;
    private List<FollowResponseDTO> accounts;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FollowResponseDTO {
        private Long accountId;
        private int score;
        private String title;
        private Long amount;
        private LocalDate date;
        private String content;
        private String imageUrl;
        private int totalLike;
        private int totalCheer;
    }
}
