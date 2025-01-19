package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.domain.account.converter.CalendarConverter;
import finance_us.finance_us.domain.account.dto.CalendarResponse;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.entity.status.AccountType;
import finance_us.finance_us.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final AccountRepository accountRepository;

    // 가계부 달력 조회
    public CalendarResponse.CalendarResponseDTO getCalendar(Integer year, Integer month, Authentication auth) {
        // userId 추출
        Long userId = (Long) auth.getPrincipal();

        // 해당 년도와 월에 해당하는 Account 데이터 조회
        List<Account> accounts = accountRepository.findByUserIdAndYearAndMonth(userId, year, month);

        // 총 별점
        double total = 0.0;
        for (Account account : accounts) {
            total += account.getScore();
        }

        // 평균 별점
        double totalScore = 0.0;
        if (!accounts.isEmpty()) {
            totalScore = total / accounts.size();
            totalScore = Math.round(totalScore * 10.0) / 10.0;  // 소수점 첫째 자리로 반올림
        }

        // 총 지출
        double totalExpense = 0.0;
        for (Account account : accounts) {
            if (account.getAccountType() == AccountType.expense) {
                totalExpense += account.getAmount();
            }
        }

        // 총 수입
        double totalIncome = 0.0;
        for (Account account : accounts) {
            if (account.getAccountType() == AccountType.income) {
                totalIncome += account.getAmount();
            }
        }

        // 날짜 리스트 생성 (중복되지 않도록 Set을 사용)
        Set<String> dates = new HashSet<>();
        for (Account account : accounts) {
            dates.add(account.getDate().toString());
        }

        // Set을 List로 변환하여 CalendarDTO 생성
        List<CalendarResponse.CalendarDTO> calendar = dates.stream()
                .map(date -> new CalendarResponse.CalendarDTO(date))
                .toList();


        // DTO 반환
        return CalendarConverter.toCalendarResponseDTO(totalScore, totalExpense, totalIncome, calendar);
    }
}
