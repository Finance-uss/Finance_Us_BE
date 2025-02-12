package finance_us.finance_us.domain.account.controller;

import finance_us.finance_us.domain.account.converter.AccountConverter;
import finance_us.finance_us.domain.account.dto.*;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.service.*;
import finance_us.finance_us.global.ApiResponse;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final LikeService likeService;
    private final AccountImageService accountImageService;
    private final GoogleOcrService googleOcrService;
    private final AccountImageExtractService accountImageExtractService;

    // 가계부 생성
    @PostMapping
    public ApiResponse<AccountResponse.AccountResponseDTO> createAccount(@RequestBody AccountRequest.AccountRequestDTO request, @RequestHeader("Authorization") String token) {
        Account account = accountService.createAccount(request, token);
        return ApiResponse.onSuccess(AccountConverter.toAccountResponseDTO(account));
    }

    // 가계부 수정
    @PatchMapping("/{accountId}")
    public ApiResponse<AccountResponse.AccountResponseDTO> updateAccount(@PathVariable Long accountId, @RequestBody AccountRequest.AccountRequestDTO request, @RequestHeader("Authorization") String token){
        Account account = accountService.updateAccount(accountId, request, token);
        return ApiResponse.onSuccess(AccountConverter.toAccountResponseDTO(account));
    }

    // 가계부 삭제
    @DeleteMapping("/{accountId}")
    public ApiResponse<Boolean> deleteAccount(@PathVariable Long accountId, @RequestHeader("Authorization") String token){
        accountService.deleteAccount(accountId, token);
        return ApiResponse.onSuccess(true);
    }

    // 가계부 활동 만족도 레포트
    @GetMapping("/report/{year}/{month}")
    public ApiResponse<ReportResponse> getReport(@PathVariable Integer year, @PathVariable Integer month,  @RequestHeader("Authorization") String token) {
        ReportResponse response = accountService.getReport(year, month, token);
        return ApiResponse.onSuccess(response);
    }

    // 가계부 특정 팔로우 조회
    @GetMapping("/follow/{followId}")
    public ApiResponse<FollowResponse> getFollow(@PathVariable Long followId,  @RequestHeader("Authorization") String token) {
        FollowResponse response = accountService.getFollow(followId, token);
        return ApiResponse.onSuccess(response);
    }

    // 좋아요 추가
    @PostMapping("/like")
    public ApiResponse<LikeResponse.LikeResponseDTO> createLike(@RequestBody LikeRequest.LikeRequestDTO request, @RequestHeader("Authorization") String token) {
        LikeResponse.LikeResponseDTO response = likeService.createLike(request, token);
        return ApiResponse.onSuccess(response);
    }

    // 응원해요 추가
    @PostMapping("/cheer")
    public ApiResponse<CheerResponse.CheerResponseDTO> createCheer(@RequestBody CheerRequest.CheerRequestDTO request, @RequestHeader("Authorization") String token) {
        CheerResponse.CheerResponseDTO response = likeService.createCheer(request, token);
        return ApiResponse.onSuccess(response);
    }

    // 영수증 인증
    @Operation(summary= "영수증 인증")
    @PostMapping(value = "/receipt", consumes = "multipart/form-data")
    public ApiResponse<AccountResponse.AccountImageResponseDTO> createAccountByReceipt(@RequestHeader("Authorization") String token,
    @RequestParam("file") MultipartFile file){
        List<String> extractedText;

        try {
            extractedText= googleOcrService.extractTextFromImage(file);
//            System.out.println("추출된 데이터: " + extractedText);
        } catch (Exception e) {
          throw new GeneralException(ErrorStatus.IMAGE_TEXT_FAILD);
        }

        return ApiResponse.onSuccess( accountImageExtractService.extractAccountFromReceipt(extractedText));

    }

    // S3 이미지 업로드
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public ApiResponse<String> uploadImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {

        String response =  accountImageService.saveImage(token, file);

        return ApiResponse.onSuccess(response); // 업로드된 파일 URL 반환


    }

}
