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

    public static AuthResponseDTO.ReadResponseDTO toReadResponseDTO(User user){
        return AuthResponseDTO.ReadResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .imgUrl(null)               // 유저로직에 이미지 들어오면 반드시 수정할 것!!
                .one_liner(user.getOne_liner())
                .role(user.getRole())
                .build();


    }

    public static User toUser(AuthRequestDTO.UpdateRequestDTO request)
    {
        return User.builder()
                .name(request.getName())
                .job(request.getJobCategory())
                .age(request.getAgeGroup())
                .one_liner(request.getOne_liner())
                // 이미지 URL 들어가야함.
                .build();

    }

}
