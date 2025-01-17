package finance_us.finance_us.domain.user.dto;

import finance_us.finance_us.domain.user.entity.status.AgeGroup;
import finance_us.finance_us.domain.user.entity.status.JobCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
