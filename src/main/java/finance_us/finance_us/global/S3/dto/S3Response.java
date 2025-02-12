package finance_us.finance_us.global.S3.dto;

import lombok.*;

public class S3Response {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class S3ResponseDTO{
        private String imageUrl;
        private String imageName;
    }
}
