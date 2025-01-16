package finance_us.finance_us.domain.user.controller;

import finance_us.finance_us.domain.user.dto.AuthRequestDTO;
import finance_us.finance_us.domain.user.dto.AuthResponseDTO;
import finance_us.finance_us.domain.user.service.UserService;
import finance_us.finance_us.global.ApiResponse;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.events.EntityReference;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/mailCheck")
    @Operation(summary = "이메일 중복확인 API", description = "이메일을 중복확인 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<Boolean> mailCheck(@RequestParam String email) {
        // 이메일 유효성 검사
        if (email == null || email.isEmpty()) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_FOUND);
        }

        if (!userService.isValidEmail(email)) {
            throw new GeneralException(ErrorStatus.EAMIL_VALIDATION_FAILED);
        }

        boolean isAvailable = userService.mailCheck(email);

        if (isAvailable) {
            return ApiResponse.onSuccess(true);
        } else {
            return ApiResponse.onFailure("COMMON400", "중복된 이메일입니다.", false);
        }
    }

    @GetMapping("/nameCheck")
    @Operation(summary = "닉네임 중복확인 API", description = "닉네임을 중복확인 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<Boolean> nameCheck(@RequestParam String name) {

        boolean isAvailable = userService.nameCheck(name);

        if (isAvailable) {
            return ApiResponse.onSuccess(true);
        } else {
            return ApiResponse.onFailure("COMMON400", "중복된 닉네임입니다.", false);
        }
    }


}
