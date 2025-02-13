package finance_us.finance_us.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

public class UserRequestDto {

    @Getter
    @Builder
    public static class imageRequestDto{
        String imageUrl;
        String imageName;
    }
}
