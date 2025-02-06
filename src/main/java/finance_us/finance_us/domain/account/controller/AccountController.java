package finance_us.finance_us.domain.account.controller;

import finance_us.finance_us.domain.account.converter.AccountConverter;
import finance_us.finance_us.domain.account.dto.*;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.service.AccountService;
import finance_us.finance_us.domain.account.service.GoogleOcrService;
import finance_us.finance_us.domain.account.service.LikeService;
import finance_us.finance_us.global.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final LikeService likeService;
    private final GoogleOcrService googleOcrService;

    // 가계부 생성
    @PostMapping
    public ApiResponse<AccountResponse.AccountResponseDTO> createAccount(@RequestBody AccountRequest.AccountRequestDTO request, Authentication authentication) {
        Account account = accountService.createAccount(request, authentication);
        return ApiResponse.onSuccess(AccountConverter.toAccountResponseDTO(account));
    }

    // 가계부 수정
    @PatchMapping("/{accountId}")
    public ApiResponse<AccountResponse.AccountResponseDTO> updateAccount(@PathVariable Long accountId, @RequestBody AccountRequest.AccountRequestDTO request, Authentication authentication){
        Account account = accountService.updateAccount(accountId, request, authentication);
        return ApiResponse.onSuccess(AccountConverter.toAccountResponseDTO(account));
    }

    // 가계부 삭제
    @DeleteMapping("/{accountId}")
    public ApiResponse<Boolean> deleteAccount(@PathVariable Long accountId, Authentication authentication){
        accountService.deleteAccount(accountId, authentication);
        return ApiResponse.onSuccess(true);
    }

    // 가계부 활동 만족도 레포트
    @GetMapping("/report/{year}/{month}")
    public ApiResponse<ReportResponse> getReport(@PathVariable Integer year, @PathVariable Integer month, Authentication authentication) {
        ReportResponse response = accountService.getReport(year, month, authentication);
        return ApiResponse.onSuccess(response);
    }

    // 가계부 특정 팔로우 조회
    @GetMapping("/follow/{followId}")
    public ApiResponse<FollowResponse> getFollow(@PathVariable Long followId, Authentication authentication) {
        FollowResponse response = accountService.getFollow(followId, authentication);
        return ApiResponse.onSuccess(response);
    }

    // 좋아요 추가
    @PostMapping("/like")
    public ApiResponse<LikeResponse.LikeResponseDTO> createLike(@RequestBody LikeRequest.LikeRequestDTO request, Authentication authentication) {
        LikeResponse.LikeResponseDTO response = likeService.createLike(request, authentication);
        return ApiResponse.onSuccess(response);
    }

    // 응원해요 추가
    @PostMapping("/cheer")
    public ApiResponse<CheerResponse.CheerResponseDTO> createCheer(@RequestBody CheerRequest.CheerRequestDTO request, Authentication authentication) {
        CheerResponse.CheerResponseDTO response = likeService.createCheer(request, authentication);
        return ApiResponse.onSuccess(response);
    }

    // 영수증 인증
    @Operation(summary= "영수증 인증")
    @PostMapping(value = "/receipt", consumes = "multipart/form-data")
    public void createAccountByReceipt(@RequestHeader("Authorization") String token,
    @RequestParam("file") MultipartFile file){
        System.out.println("야호");
        // ✅ 1. AWS Textract를 이용하여 영수증에서 텍스트 추출
        try {
//            List<String> extractedText = textractService.extractTextFromReceipt(file);
            List<String> extractedText = googleOcrService.extractTextFromImage(file);
            System.out.println("추출된 영수증 데이터: " + extractedText);
        } catch (Exception e) {
            System.out.println("실패! 예외 메시지: " + e.getMessage());
            e.printStackTrace(); // 전체 스택 트레이스 출력
        }
//
//        // ✅ 2. 추출된 데이터를 바탕으로 가계부(Account) 생성
////        Account account = accountService.createAccountFromText(extractedText);
//
//
//        // ✅ 3. 응답 객체로 변환하여 반환
////        return ApiResponse.onSuccess(AccountConverter.toAccountResponseDTO(account));
    }

}
