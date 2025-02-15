package finance_us.finance_us.domain.user.dto;

import finance_us.finance_us.domain.user.entity.status.AgeGroup;
import finance_us.finance_us.domain.user.entity.status.JobCategory;
import finance_us.finance_us.domain.user.entity.status.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

public class AuthRequestDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequestDTO{
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignRequestDTO{
        private String email;
        private String username;
        private String password;
        private JobCategory jobCategory;
        private AgeGroup ageGroup;
        private String one_liner;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequestDTO{
        private String name;
        private JobCategory jobCategory;
        private AgeGroup ageGroup;
        private String one_liner;

        String imgUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class userAuthRequestDTO{
        private String content;
        private String imgUrl;

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class discordAuthRequestDTO{
        private Long userId;
        private Boolean approved;
        private String imgUrl;
    }
}
