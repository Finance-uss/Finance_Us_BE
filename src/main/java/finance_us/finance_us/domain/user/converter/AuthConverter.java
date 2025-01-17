package finance_us.finance_us.domain.user.converter;

import finance_us.finance_us.domain.user.dto.AuthRequestDTO;
import finance_us.finance_us.domain.user.dto.AuthResponseDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.entity.status.Role;

public class AuthConverter {

    public static User toUser(AuthRequestDTO.SignRequestDTO request, Role role){

        return User.builder()
                .age(request.getAgeGroup())
                .email(request.getEmail())
                .role(role)
                .job(request.getJobCategory())
                .password(request.getPassword())
                .one_liner(request.getUsername())
                .name(request.getUsername())
                .build();

    }

    public static AuthResponseDTO.SignResponseDTO toSigninResponseDTO(User user){
        return AuthResponseDTO.SignResponseDTO.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .id(user.getId())
                .build();

    }

    public static AuthResponseDTO.UserResponseDTO toUserResponseDTO(User user){
        return AuthResponseDTO.UserResponseDTO.builder()
                .userId(user.getId())
                .isAuthenticated(user.isAuthenticated())
                .build();
    }
}
