package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.domain.account.converter.CalendarConverter;
import finance_us.finance_us.domain.account.dto.CalendarResponse;
import finance_us.finance_us.domain.account.entity.Account;
import finance_us.finance_us.domain.account.entity.status.AccountType;
import finance_us.finance_us.domain.account.repository.AccountRepository;
import finance_us.finance_us.domain.user.entity.UserPreference;
import finance_us.finance_us.domain.user.repository.UserPreferenceRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final AccountRepository accountRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final TokenProvider tokenProvider;

    // 가계부 달력 조회
    public CalendarResponse.CalendarResponseDTO getCalendar(Integer year, Integer month, String token) {
        // userId 추출
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // 해당 년도와 월에 해당하는 Account 데이터 조회
        List<Account> accounts = accountRepository.findByUserIdAndYearAndMonth(userId, year, month);


        // 유저 환경설정 조회
        Optional<UserPreference> userPreference = userPreferenceRepository.findByUserId(userId);


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

        // 하이라이트 스위치가 꺼져 있으면 예외 발생
        if (userPreference.isPresent() && Boolean.FALSE.equals(userPreference.get().getHighlightSwitch())) {
            return CalendarConverter.toCalendarResponseDTO(totalScore, totalExpense, totalIncome, new ArrayList<>());
        }


        // 환경설정이 없으면 calendar를 빈 리스트로 설정
        List<CalendarResponse.CalendarDTO> calendar = new ArrayList<>();
        if (userPreference.isPresent()) {
            // 환경설정에서 값 가져오기
            Integer incomeAmount = userPreference.get().getIncomeAmount();
            Integer expenseAmount = userPreference.get().getExpenseAmount();
            String incomeColor = userPreference.get().getIncomeColor();
            String expenseColor = userPreference.get().getExpenseColor();

            // 날짜별 income과 expense 합산
            Map<String, Long> incomeMap = new HashMap<>();
            Map<String, Long> expenseMap = new HashMap<>();

            for (Account account : accounts) {
                String date = account.getDate().toString();

                if (account.getAccountType() == AccountType.income) {
                    incomeMap.merge(date, account.getAmount(), Long::sum);
                }

                if (account.getAccountType() == AccountType.expense) {
                    expenseMap.merge(date, account.getAmount(), Long::sum);
                }
            }

            for (String date : expenseMap.keySet()) {
                Long expenseTotal = expenseMap.get(date);
                if (expenseTotal >= expenseAmount) {
                    calendar.add(new CalendarResponse.CalendarDTO(date, expenseColor));
                }
            }

            for (String date : incomeMap.keySet()) {
                Long incomeTotal = incomeMap.get(date);
                if (incomeTotal >= incomeAmount && !expenseMap.containsKey(date)) {
                    calendar.add(new CalendarResponse.CalendarDTO(date, incomeColor));
                }
            }
        }




        // DTO 반환
        return CalendarConverter.toCalendarResponseDTO(totalScore, totalExpense, totalIncome, calendar);
    }



    // 가계부 달별 일별 조회
    public List<Account> getCalendarDetail(Integer year, Integer month, Integer day, String token) {
        // userId 추출
        Long userId = tokenProvider.extractUserIdFromToken(token);

        // 해당 년도, 월, 일에 해당하는 Account 데이터를 조회
        return accountRepository.findByUserIdAndYearAndMonthAndDay(userId, year, month, day);
    }

}
