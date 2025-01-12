package finance_us.finance_us.domain.user.service;

import finance_us.finance_us.domain.user.converter.UserConverter;
import finance_us.finance_us.domain.user.dto.UserRequestDTO;
import finance_us.finance_us.domain.user.dto.UserResponseDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.security.TokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponseDTO.SignResponseDTO signUp(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User email already exists");
        }

        validatePassword(user.getPassword()); // 비밀번호 검증

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return UserConverter.toSigninResponseDTO(user);
    }

    private void validatePassword(String password) {
        // 길이 검사
        if (password.length() < 8 || password.length() > 12) {
            throw new RuntimeException("비밀번호는 8자 이상 12자 이내여야 합니다.");
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
            throw new RuntimeException("비밀번호는 영어 대/소문자, 숫자 중 2종류 이상을 조합해야 합니다.");
        }
    }



    public UserResponseDTO.LoginResponseDTO login(UserRequestDTO.LoginRequestDTO loginRequestDTO) {
        Optional<User> optionalUser = userRepository.findByName(loginRequestDTO.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
                String token = tokenProvider.generateToken(user.getName(), user.getId());
                return new UserResponseDTO.LoginResponseDTO(token);
            }
        }
        throw new RuntimeException("Invalid username or password");
    }

    public UserResponseDTO.LoginResponseDTO refreshToken(String token) {
        // 토큰에서 클레임 추출
        Claims claims = tokenProvider.extractClaims(token);

        // 클레임에서 사용자 정보 추출
        String username = claims.getSubject();
        Long userId = claims.get("userId", Long.class);

        // 사용자 확인
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 새 토큰 발급
        String newToken = tokenProvider.generateToken(user.getName(), user.getId());
        return new UserResponseDTO.LoginResponseDTO(newToken);
    }
}
