package finance_us.finance_us.domain.user.controller;

import com.amazonaws.services.ec2.model.AssignPrivateIpAddressesRequest;
import finance_us.finance_us.domain.user.dto.AuthRequestDTO;
import finance_us.finance_us.domain.user.dto.AuthResponseDTO;
import finance_us.finance_us.domain.user.service.AuthService;
import finance_us.finance_us.domain.user.service.UserService;
import finance_us.finance_us.global.ApiResponse;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.global.file.S3FileService;
import finance_us.finance_us.security.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.events.EntityReference;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthService authService;
    private final S3FileService s3FileService;

    @GetMapping("/mailCheck")
    @Operation(summary = "이메일 중복확인 API", description = "이메일을 중복확인 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<String> mailCheck(@RequestParam String email) {

       userService.mailCheck(email);

       return ApiResponse.onSuccess("사용가능한 이메일 입니다.");
    }

    @GetMapping("/nameCheck")
    @Operation(summary = "닉네임 중복확인 API", description = "닉네임을 중복확인 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<String> nameCheck(@RequestParam String name) {

        userService.nameCheck(name);

        return ApiResponse.onSuccess("사용가능한 닉네임 입니다.");

    }

    @PatchMapping("/resetMail")
    @Operation(summary = "이메일 변경 API", description = "이메일을 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<Map<String,Object>> resetMail(@RequestHeader("Authorization") String token, @RequestParam String email) {

        Long userId = tokenProvider.extractUserIdFromToken(token);

        userService.mailCheck(email);
        userService.changeMail(userId, email);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("updatedField", "email");

        return ApiResponse.onSuccess(response);

    }

    @PatchMapping("/resetPassword")
    @Operation(summary = "비밀번호 변경 API", description = "비밀번호를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<Map<String,Object>> resetPassword(@RequestParam String email, String password) {

        userService.changePassword(email, password);

        Map<String, Object> response = new HashMap<>();
        response.put("updatedField", "password");

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/findMail")
    @Operation(summary = "이메일 찾기 API", description = "사용자의 이메일을 찾습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<Map<String,Object>> findMail(@RequestParam String name) {

        String email = userService.findEmail(name);
        Map<String, Object> response = new HashMap<>();
        response.put("Email",email);

        return ApiResponse.onSuccess(response);
    }

    @PostMapping(value = "/image", consumes = "multipart/form-data")
    @Operation(summary = "사용자 프로필 사진 업로드", description = "사용자의 프로필 사진을 업로드합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ApiResponse<Map<String, Object>> uploadImage(
            @RequestHeader("Authorization") String token,
            @Parameter(
                    description = "업로드할 파일",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("file") MultipartFile file) {

        // DB에 이미지 URL 저장
        String imageUrl =  userService.saveImage(token, file);

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("profileImageUrl", imageUrl);

        return ApiResponse.onSuccess(response); // 업로드된 파일 URL 반환


    }

    @PatchMapping(value = "/image", consumes = "multipart/form-data")
    @Operation(summary = "사용자 프로필 사진 수정", description = "사용자의 프로필 사진을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ApiResponse<Map<String, Object>> resetImage(
            @RequestHeader("Authorization") String token,
            @Parameter(
                    description = "수정 파일",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("file") MultipartFile file) {

        // 이전 이미지 삭제
        userService.deleteImage(token);

        // DB에 이미지 URL 저장
        String imageUrl =  userService.saveImage(token, file);

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("profileImageUrl", imageUrl);

        return ApiResponse.onSuccess(response); // 업로드된 파일 URL 반환

    }

    @DeleteMapping(value = "/image")
    @Operation(summary = "사용자 프로필 사진 삭제", description = "사용자의 프로필 사진을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러")
    })
    public ApiResponse<String> deleteImage(
            @RequestHeader("Authorization") String token){

        // 이전 이미지 삭제
        userService.deleteImage(token);

        return ApiResponse.onSuccess("삭제 완료되었습니다. ");
    }


    //회원탈퇴 Delete / api/user/
    @DeleteMapping()
    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<String> withDrawUser(@RequestHeader("Authorization") String token) {

        Long userId = tokenProvider.extractUserIdFromToken(token);
        userService.deleteUser(userId);
        return ApiResponse.onSuccess("삭제 완료되었습니다. ");
    }

    @GetMapping()
    @Operation(summary = "회원 조회 API", description = "회원을 조회합니다.")
    public ApiResponse<AuthResponseDTO.ReadResponseDTO> readUser(@RequestHeader("Authorization") String token) {

        Long userId = tokenProvider.extractUserIdFromToken(token);

        return ApiResponse.onSuccess(userService.readUser(userId));
    }

    @PatchMapping()
    @Operation(summary = "회원 수정 API", description = "회원을 수정합니다.")
    public ApiResponse<String> updateUser(@RequestHeader("Authorization") String token, AuthRequestDTO.UpdateRequestDTO updateRequestDTO) {

        Long userId = tokenProvider.extractUserIdFromToken(token);

        return ApiResponse.onSuccess(userService.updateUser(userId, updateRequestDTO));
    }


}
