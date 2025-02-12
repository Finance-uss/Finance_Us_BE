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
public class ReportResponse {
    private List<ReportResponseDTO> reduceActivity;
    private List<ReportResponseDTO> satisfactoryActivity;
    private List<ReportResponseDTO> maintainActivity;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReportResponseDTO {
        private Long accountId;
        private Integer score;
        private String title;
        private Long amount;
        private LocalDate date;
        private String subName;
        private String imageUrl;
        private String imageName;
    }
}
