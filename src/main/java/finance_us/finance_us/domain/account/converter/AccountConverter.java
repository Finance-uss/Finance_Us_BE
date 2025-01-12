package finance_us.finance_us.domain.account.converter;

import finance_us.finance_us.domain.account.dto.AccountResponse;
import finance_us.finance_us.domain.account.entity.Account;

public class AccountConverter {

    public static AccountResponse.AccountResponseDTO toAccountResponseDTO(Account account){
        return AccountResponse.AccountResponseDTO.builder()
                .accountId(account.getId())
                .build();
    }
}
