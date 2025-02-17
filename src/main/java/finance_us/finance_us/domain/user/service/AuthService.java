package finance_us.finance_us.domain.user.service;

import finance_us.finance_us.domain.user.converter.AuthConverter;
import finance_us.finance_us.domain.user.dto.AuthRequestDTO;
import finance_us.finance_us.domain.user.dto.AuthResponseDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.entity.UserPreference;
import finance_us.finance_us.domain.user.repository.UserPreferenceRepository;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.security.TokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final TokenProvider tokenProvider;
    private final DiscordWebhookService discordWebhookService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponseDTO.SignResponseDTO signUp(User user) {
        validatePassword(user.getPassword()); // 비밀번호 검증

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return AuthConverter.toSigninResponseDTO(user);
    }



    public void validatePassword(String password) {
        // 길이 검사
        if (password.length() < 8 || password.length() > 12) {
            throw new GeneralException(ErrorStatus.PASSWORD_VALIDATION_FAILED);
        }

        // 영어 대문자, 소문자, 숫자 포함 여부 검사
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        // 최소 2종류 이상 포함 여부 확인
        int count = 0;
        if (hasUpperCase) count++;
        if (hasLowerCase) count++;
        if (hasDigit) count++;

        if (count < 2) {
            throw new GeneralException(ErrorStatus.PASSWORD_VALIDATION_FAILED);
        }
    }

    // 이메일 형식 검증
    public void isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if(!email.matches(emailRegex))
            throw new GeneralException(ErrorStatus.EMAIL_VALIDATION_FAILED);

    }

    //로그인
    public AuthResponseDTO.LoginResponseDTO login(AuthRequestDTO.LoginRequestDTO loginRequestDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequestDTO.getEmail());
        if (optionalUser.isEmpty()) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        String token = tokenProvider.generateToken(user.getName(), user.getId());
        return new AuthResponseDTO.LoginResponseDTO(token);
    }

    //토큰 재발행
    public String refreshToken(String token) {
        // 토큰에서 클레임 추출
        Claims claims = tokenProvider.extractClaims(token);

        // 클레임에서 사용자 정보 추출
        String username = claims.getSubject();

        // 사용자 확인
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 새 토큰 발급
        String newToken = tokenProvider.generateToken(user.getName(), user.getId());
        return newToken;
    }

    //사용자 인증
    public AuthResponseDTO.UserResponseDTO authUser(String token, AuthRequestDTO.userAuthRequestDTO userAuthRequestDTO){
        Long userId = tokenProvider.extractUserIdFromToken(token);

        //사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        discordWebhookService.sendAuthRequest(user, userAuthRequestDTO);

        return AuthConverter.toUserResponseDTO(user);
    }

    //디스코드 승인/거절 요청
    public void verifyUser(Long userId, boolean isApproved){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        user.setAuthenticated(isApproved); // 승인 or 거절 반영
        userRepository.save(user);
    }
    // 회원가입 시에 기본적으로 설정이 있어야 함.
    public Long createUserPreference(Long userId)
    {
        var p = UserPreference.builder()
                .alramSwitch(true)
                .openSwitch(true)
                .highlightSwitch(true)
                .expenseAmount(100000)
                .incomeAmount(100000)
                .expenseColor("#FFB55D")
                .incomeColor("#8396C3")
                .user(User.builder().Id(userId).build())
                .build();

        return userPreferenceRepository.save(p).getId();
    }
    // 유저 선호도 불러오기
    public AuthResponseDTO.UserPreferenceResponseDTO getUserPreference(String token){
        Long userId = tokenProvider.extractUserIdFromToken(token);
        var userPreference = userPreferenceRepository
                            .findByUserId(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_PREFERENCE_NOT_FOUND));

        return AuthConverter.toUserPreferenceResponseDTO(userPreference);
    }

    public String updateUserPreference(String token, AuthRequestDTO.UserPreferenceRequestDTO dto)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        var userPreference = userPreferenceRepository.findByUserId(userId)
                            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_PREFERENCE_NOT_FOUND));

        String updateStr = "";
        if(dto.getAlarmSwitch() != null) {
            userPreference.setAlramSwitch(dto.getAlarmSwitch());
            updateStr += "alarmSwitch ";
        }
        if(dto.getOpenSwitch() != null) {
            userPreference.setOpenSwitch(dto.getOpenSwitch());
            updateStr += "openSwitch ";
        }
        if(dto.getHighlightSwitch() != null) {
            userPreference.setHighlightSwitch(dto.getHighlightSwitch());
            updateStr += "highlightSwitch ";
        }
        if(dto.getExpenseAmount() != null) {
            userPreference.setExpenseAmount(dto.getExpenseAmount());
            updateStr += "expenseAmount ";
        }
        if(dto.getIncomeAmount() != null) {
            userPreference.setIncomeAmount(dto.getIncomeAmount());
            updateStr += "incomeAmount ";
        }
        if(dto.getExpenseColor() != null) {
            userPreference.setExpenseColor(dto.getExpenseColor());
            updateStr += "expenseColor ";
        }
        if(dto.getIncomeColor() != null) {
            userPreference.setIncomeColor(dto.getIncomeColor());
            updateStr += "incomeColor ";
        }

        userPreferenceRepository.save(userPreference);

        return "updated field : " + updateStr;
    }

}
