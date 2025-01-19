package finance_us.finance_us.domain.account.controller;

import finance_us.finance_us.domain.account.converter.AccountConverter;
import finance_us.finance_us.domain.account.dto.AccountRequest;
import finance_us.finance_us.domain.account.dto.AccountResponse;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.service.AccountService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 가계부 생성
    @PostMapping
    public ApiResponse<AccountResponse.AccountResponseDTO> createAccount(@RequestBody AccountRequest.AccountRequestDTO request, Authentication authentication) {
        Account account = accountService.createAccount(request, authentication);
        return ApiResponse.onSuccess(AccountConverter.toAccountResponseDTO(account));
    }

    // 가계부 수정
    @PatchMapping("/{accountId}")
    public ApiResponse<AccountResponse.AccountResponseDTO> updateAccount(@PathVariable Long accountId, @RequestBody AccountRequest.AccountRequestDTO request){
        Account account = accountService.updateAccount(accountId, request);
        return ApiResponse.onSuccess(AccountConverter.toAccountResponseDTO(account));
    }

    // 가계부 삭제
    @DeleteMapping("/{accountId}")
    public ApiResponse<Boolean> deleteAccount(@PathVariable Long accountId){
        accountService.deleteAccount(accountId);
        return ApiResponse.onSuccess(true);
    }

}
