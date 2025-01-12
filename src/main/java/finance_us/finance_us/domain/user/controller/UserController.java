package finance_us.finance_us.domain.user.controller;

import finance_us.finance_us.domain.user.converter.UserConverter;
import finance_us.finance_us.domain.user.dto.UserRequestDTO;
import finance_us.finance_us.domain.user.dto.UserResponseDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.entity.status.Role;
import finance_us.finance_us.domain.user.service.UserService;
import finance_us.finance_us.global.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<UserResponseDTO.LoginResponseDTO> login(@RequestBody UserRequestDTO.LoginRequestDTO loginRequestDTO) {
        return ApiResponse.onSuccess(userService.login(loginRequestDTO));
    }

    @PostMapping("/adminSignup")
    public ApiResponse<UserResponseDTO.SignResponseDTO> adminSignin(@RequestBody UserRequestDTO.SignRequestDTO signRequestDTO) {
        User user = UserConverter.toUser(signRequestDTO, Role.ADMIN);
        return ApiResponse.onSuccess(userService.signUp(user));
    }

    @PostMapping("/userSignup")
    public  ApiResponse<UserResponseDTO.SignResponseDTO> userSignin(@RequestBody UserRequestDTO.SignRequestDTO signRequestDTO) {
        User user = UserConverter.toUser(signRequestDTO, Role.USER);
        return ApiResponse.onSuccess(userService.signUp(user));
    }

    @PostMapping("/refresh")
    public ApiResponse<String> refreshToken (@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ApiResponse.onSuccess(userService.refreshToken(token));
    }
}
