package finance_us.finance_us.domain.account.controller;

import finance_us.finance_us.domain.account.converter.AccountConverter;
import finance_us.finance_us.domain.account.dto.AccountRequest;
import finance_us.finance_us.domain.account.dto.AccountResponse;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.service.AccountService;
import finance_us.finance_us.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    @PostMapping
    public ApiResponse<AccountResponse.AccountResponseDTO> createAccount(@RequestBody AccountRequest.AccountRequestDTO request){

        Account account = accountService.createAccount(request);
        return ApiResponse.onSuccess(AccountConverter.toAccountResponseDTO(account));

    }

}
