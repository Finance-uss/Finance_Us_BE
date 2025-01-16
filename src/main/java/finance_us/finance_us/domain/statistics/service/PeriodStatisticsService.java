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

    public PeriodStatisticsResponse getYearlyStatistics(Long year, String type){
        Type statisticsType = Type.valueOf(type.toUpperCase());
        List<PeriodStatistics> statistics = periodStatisticsRepository.findByYearAndType(year, statisticsType);

        List<PeriodStatisticsResponse.MonthData> monthlyData = statistics.stream()
                .map(stat -> new PeriodStatisticsResponse.MonthData(stat.getMonth(), stat.getTotalMoney()))
                .collect(Collectors.toList());

        return new PeriodStatisticsResponse(year, type, monthlyData);
    }

    @Transactional(readOnly = true)
    public MonthDetailResponse getMonthlyDetail(Long year, Long month, AccountType type){
        LocalDate startDate = LocalDate.of(year.intValue(), month.intValue(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Account> accounts = accountRepository.findBytDateBetweenAndAccountType(startDate, endDate, type);
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
    public void updatePeriodStatistics(Long year, Long month, AccountType accountType, User user) {
        Type type = accountType == AccountType.expense ? Type.EXPENSE : Type.INCOME;

        Long totalMoney = accountRepository.findByYearAndMonthAndAccountType(year, month, accountType, user)
                .stream()
                .mapToLong(Account::getAmount)
                .sum();

        periodStatisticsRepository.updateTotalMoney(year, month, type, totalMoney, user);
    }
}
