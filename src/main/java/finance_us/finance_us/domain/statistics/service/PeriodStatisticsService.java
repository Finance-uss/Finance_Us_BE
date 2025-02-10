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

    //가계부 생성 시 기간별 통계 데이터 관리
    @Transactional
    public void updatePeriodStatisticsOnCreate(Account account) {
        Long year = (long) account.getDate().getYear();
        Long month = (long) account.getDate().getMonthValue();
        Long userId = account.getUser().getId();
        Type type = Type.valueOf(account.getAccountType().name().toUpperCase());

        //기존 통계 데이터 조회
        PeriodStatistics statistics = periodStatisticsRepository
                .findByYearAndMonthAndTypeAndUserId(year, month, type, userId)
                .orElse(null);

        // 통계 데이터가 없을 때 생성
        if (statistics == null) {
            statistics = PeriodStatistics.builder()
                    .year(year)
                    .month(month)
                    .type(type)
                    .totalMoney(account.getAmount())
                    .user(account.getUser())
                    .build();

            periodStatisticsRepository.save(statistics);
        }
        //통계 데이터가 있을 때 업데이트
        else {
            statistics.setTotalMoney(statistics.getTotalMoney() + account.getAmount());
        }
    }

    //가계부 수정 시 기간별 통계 업데이트
    @Transactional
    public void updatePeriodStatisticsOnUpdate(Account oldAccount, Account updatedAccount) {
        Long year = (long) updatedAccount.getDate().getYear();
        Long month = (long) updatedAccount.getDate().getMonthValue();
        Long userId = updatedAccount.getUser().getId();
        Type type = Type.valueOf(updatedAccount.getAccountType().name().toUpperCase());

        // 기존 유형과 변경 후 유형이 다르면, 기존 통계에서 제거 후 새로운 통계에 추가
        Type oldType = Type.valueOf(oldAccount.getAccountType().name().toUpperCase());
        Type newType = Type.valueOf(updatedAccount.getAccountType().name().toUpperCase());

        if (!oldType.equals(newType)) {
            //기존 가계부의 유형에 맞는 통계 수정
            PeriodStatistics oldStatistics = periodStatisticsRepository
                    .findByYearAndMonthAndTypeAndUserId(year, month, oldType, userId)
                    .orElse(null);

            if (oldStatistics != null) {
                oldStatistics.setTotalMoney(oldStatistics.getTotalMoney() - oldAccount.getAmount());
            }

            //수정된 가계부의 유형에 맞는 통계 수정
            PeriodStatistics newStatistics = periodStatisticsRepository
                    .findByYearAndMonthAndTypeAndUserId(year, month, newType, userId)
                    .orElse(null);

            if (newStatistics == null) {
                //수정된 가계부 유형에 맞는 통계가 없으면 생성
                newStatistics = PeriodStatistics.builder()
                        .year(year)
                        .month(month)
                        .type(newType)
                        .totalMoney(updatedAccount.getAmount())
                        .user(updatedAccount.getUser())
                        .build();
                periodStatisticsRepository.save(newStatistics);
            } else {
                // 새로운 가계부 유형에 맞는 통계가 있으면 업데이트
                newStatistics.setTotalMoney(newStatistics.getTotalMoney() + updatedAccount.getAmount());
            }
        } else {
            //같은 유형이면 금액만 수정
            PeriodStatistics statistics = periodStatisticsRepository
                    .findByYearAndMonthAndTypeAndUserId(year, month, newType, userId)
                    .orElse(null);

            if (statistics != null) {
                statistics.setTotalMoney(statistics.getTotalMoney() - oldAccount.getAmount() + updatedAccount.getAmount());
            }
        }
    }

    //가계부 삭제 시 통계 업데이트
    @Transactional
    public void updatePeriodStatisticsOnDelete(Account account) {
        Long year = (long) account.getDate().getYear();
        Long month = (long) account.getDate().getMonthValue();
        Long userId = account.getUser().getId();
        Type type = Type.valueOf(account.getAccountType().name().toUpperCase());

        // 기존 데이터 조회
        PeriodStatistics statistics = periodStatisticsRepository
                .findByYearAndMonthAndTypeAndUserId(year, month, type, userId)
                .orElse(null);

        if (statistics != null) {
            //기존 통계에서 금액 차감
            statistics.setTotalMoney(statistics.getTotalMoney() - account.getAmount());
        }
    }


}
