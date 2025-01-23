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
public class AccountReportResponse {
    private List<AccountReportDTO> reduceActivity;
    private List<AccountReportDTO> satisfactoryActivity;
    private List<AccountReportDTO> maintainActivity;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountReportDTO {
        private Long accountId;
        private Integer score;
        private String title;
        private Long amount;
        private LocalDate date;
        private String subName;
        private String imageUrl;
    }
}
