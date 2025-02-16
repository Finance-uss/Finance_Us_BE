package finance_us.finance_us.domain.user.dto;

import finance_us.finance_us.domain.user.entity.status.AgeGroup;
import finance_us.finance_us.domain.user.entity.status.JobCategory;
import finance_us.finance_us.domain.user.entity.status.Role;
import lombok.*;

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
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserPreferenceRequestDTO{
        Boolean openSwitch;
        Boolean alarmSwitch;
        Boolean highlightSwitch;

        Integer expenseAmount;
        Integer incomeAmount;
        String expenseColor;
        String incomeColor;
    }

}
