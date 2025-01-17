package finance_us.finance_us.domain.user.controller;

import finance_us.finance_us.domain.user.converter.AuthConverter;
import finance_us.finance_us.domain.user.dto.AuthRequestDTO;
import finance_us.finance_us.domain.user.dto.AuthResponseDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.entity.status.Role;
import finance_us.finance_us.domain.user.service.AuthService;
import finance_us.finance_us.domain.user.service.MailService;
import finance_us.finance_us.global.ApiResponse;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "인증 관련 API")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MailService mailService;

    @PostMapping("/login")
    @Operation(summary = "사용자 로그인 API", description = "사용자가 이메일과 비밀번호를 사용하여 로그인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<AuthResponseDTO.LoginResponseDTO> login(@RequestBody AuthRequestDTO.LoginRequestDTO loginRequestDTO) {
        return ApiResponse.onSuccess(authService.login(loginRequestDTO));
    }

    @PostMapping("/adminSignup")
    @Operation(summary = "관리자 회원가입 API", description = "관리자 계정을 생성하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<AuthResponseDTO.SignResponseDTO> adminSignin(@RequestBody AuthRequestDTO.SignRequestDTO signRequestDTO) {
        User user = AuthConverter.toUser(signRequestDTO, Role.ADMIN);
        return ApiResponse.onSuccess(authService.signUp(user));
    }

    @PostMapping("/userSignup")
    @Operation(summary = "사용자 회원가입 API", description = "일반 사용자 계정을 생성하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
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
    public ApiResponse<Map<String, String>> refreshToken(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String newToken = authService.refreshToken(token);

        // 응답 데이터 구성
        Map<String, String> response = new HashMap<>();
        response.put("token", newToken);

        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/mailSend")
    @Operation(summary = "이메일 인증코드 전송", description = "이메일 인증코드를 전송합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<String> mailSend(@RequestBody String email) {
        if (email == null || email.isEmpty()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_FOUND);
        }
        mailService.sendMail(email);
        return ApiResponse.onSuccess("인증 코드가 이메일로 전송되었습니다.");
    }


    @GetMapping("/numberCheck")
    @Operation(summary = "이메일 인증코드 번호 체크", description = "전송된 이메일 인증코드와 번호를 체크합니다. ")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<Boolean> numberCheck(@RequestParam String email, Integer number) {

        if (mailService.checkVerificationNumber(email, number))
            return ApiResponse.onSuccess(true);

        return ApiResponse.onFailure("COMMON400", "이메일 인증번호와 다릅니다.", false);
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "사용자 인증", description = "사용자의 권한을 인증합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<AuthResponseDTO.UserResponseDTO> authUser(@PathVariable Long userId) {
        return ApiResponse.onSuccess(authService.authUser(userId));

    }
}
