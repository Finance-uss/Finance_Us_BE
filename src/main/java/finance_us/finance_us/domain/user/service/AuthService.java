package finance_us.finance_us.domain.user.service;

import finance_us.finance_us.domain.user.converter.AuthConverter;
import finance_us.finance_us.domain.user.dto.AuthRequestDTO;
import finance_us.finance_us.domain.user.dto.AuthResponseDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.security.TokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponseDTO.SignResponseDTO signUp(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        validatePassword(user.getPassword()); // 비밀번호 검증

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return AuthConverter.toSigninResponseDTO(user);
    }

    private void validatePassword(String password) {
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


    public AuthResponseDTO.LoginResponseDTO login(AuthRequestDTO.LoginRequestDTO loginRequestDTO) {
        Optional<User> optionalUser = userRepository.findByName(loginRequestDTO.getUsername());
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
}
