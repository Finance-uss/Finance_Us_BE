package finance_us.finance_us.domain.user.controller;

import finance_us.finance_us.domain.user.converter.AuthConverter;
import finance_us.finance_us.domain.user.dto.AuthRequestDTO;
import finance_us.finance_us.domain.user.dto.AuthResponseDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.entity.status.Role;
import finance_us.finance_us.domain.user.service.AuthService;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "인증 관련 API")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "사용자 로그인 API", description = "사용자가 이메일과 비밀번호를 사용하여 로그인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<AuthResponseDTO.LoginResponseDTO> login(@RequestBody AuthRequestDTO.LoginRequestDTO loginRequestDTO) {
        return ApiResponse.onSuccess(authService.login(loginRequestDTO));
    }

    @PostMapping("/adminSignup")
    @Operation(summary = "관리자 회원가입 API", description = "관리자 계정을 생성하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<AuthResponseDTO.SignResponseDTO> adminSignin(@RequestBody AuthRequestDTO.SignRequestDTO signRequestDTO) {
        User user = AuthConverter.toUser(signRequestDTO, Role.ADMIN);
        return ApiResponse.onSuccess(authService.signUp(user));
    }

    @PostMapping("/userSignup")
    @Operation(summary = "사용자 회원가입 API", description = "일반 사용자 계정을 생성하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<AuthResponseDTO.SignResponseDTO> userSignin(@RequestBody AuthRequestDTO.SignRequestDTO signRequestDTO) {
        User user = AuthConverter.toUser(signRequestDTO, Role.USER);
        return ApiResponse.onSuccess(authService.signUp(user));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Access 토큰 재발급 API", description = "만료된 access 토큰을 새로 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "Authorization", description = "Bearer access 토큰", required = true)
    })
    public ApiResponse<String> refreshToken(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ApiResponse.onSuccess(authService.refreshToken(token));
    }
}
