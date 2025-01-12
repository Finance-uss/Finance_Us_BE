package finance_us.finance_us.domain.user.converter;

import finance_us.finance_us.domain.user.dto.UserRequestDTO;
import finance_us.finance_us.domain.user.dto.UserResponseDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.entity.status.Role;

import java.util.ArrayList;

public class UserConverter {

    public static User toUser(UserRequestDTO.SignRequestDTO request, Role role){

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

    public static UserResponseDTO.SignResponseDTO toSigninResponseDTO(User user){
        return UserResponseDTO.SignResponseDTO.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .id(user.getId())
                .build();

    }
}
