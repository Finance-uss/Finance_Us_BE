package finance_us.finance_us.domain.user.dto;

import finance_us.finance_us.domain.user.entity.status.Role;
import lombok.*;

@Builder
public class UserResponseDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginResponseDTO{
        private String token;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignResponseDTO{
        private String email;
        private String name;
        private Long id;
        private Role role;
    }
}
