package finance_us.finance_us.domain.statistics.service;

import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.entity.status.AccountType;
import finance_us.finance_us.domain.account.repository.AccountRepository;
import finance_us.finance_us.domain.statistics.dto.MonthDetailResponse;
import finance_us.finance_us.domain.statistics.dto.PeriodStatisticsResponse;
import finance_us.finance_us.domain.statistics.entity.PeriodStatistics;
import finance_us.finance_us.domain.statistics.entity.status.Type;
import finance_us.finance_us.domain.statistics.repository.PeriodStatisticsRepository;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeriodStatisticsService {
    private final PeriodStatisticsRepository periodStatisticsRepository;
    private final AccountRepository accountRepository;
    private final TokenProvider tokenProvider;

    public PeriodStatisticsResponse getYearlyStatistics(String token, Long year, String type){
        Long userId = tokenProvider.extractUserIdFromToken(token);
        Type statisticsType = Type.valueOf(type.toUpperCase());

        List<PeriodStatistics> statistics = periodStatisticsRepository.findByYearAndTypeAndUserId(year, statisticsType, userId);

        List<PeriodStatisticsResponse.MonthData> monthlyData = statistics.stream()
                .map(stat -> new PeriodStatisticsResponse.MonthData(stat.getMonth(), stat.getTotalMoney()))
                .collect(Collectors.toList());

        return new PeriodStatisticsResponse(year, type, monthlyData);
    }

    @Transactional(readOnly = true)
    public MonthDetailResponse getMonthlyDetail(String token, Long year, Long month, AccountType type){
        Long userId = tokenProvider.extractUserIdFromToken(token);
        LocalDate startDate = LocalDate.of(year.intValue(), month.intValue(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Account> accounts = accountRepository.findByDateBetweenAndAccountTypeAndUserId(startDate, endDate, type, userId);
        List<MonthDetailResponse.Detail> details = accounts.stream()
                .map(account -> new MonthDetailResponse.Detail(
                        Long.valueOf(account.getDate().getDayOfMonth()),
                        account.getTitle(),
                        account.getAmount()
                ))
                .collect(Collectors.toList());
        Long totalMoney = accounts.stream()
                .mapToLong(Account::getAmount)
                .sum();
        return new MonthDetailResponse(year, month, type.name(), totalMoney, details);
    }

    @Transactional
    public void updatePeriodStatistics(String token, Long year, Long month, AccountType accountType) {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        Type type = accountType == AccountType.expense ? Type.EXPENSE : Type.INCOME;

        Long totalMoney = accountRepository.findByYearAndMonthAndAccountTypeAndUserId(year, month, accountType, userId)
                .stream()
                .mapToLong(Account::getAmount)
                .sum();

        periodStatisticsRepository.updateTotalMoney(year, month, type, totalMoney, userId);
    }
}
