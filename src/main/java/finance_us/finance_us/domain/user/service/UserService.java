package finance_us.finance_us.domain.user.service;

import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //이메일 중복 확인
    public void mailCheck(String email) {
        authService.isValidEmail(email);
        if(!userRepository.findByEmail(email).isEmpty())
            throw new GeneralException(ErrorStatus.EMAIL_EXIST);
    }

    //닉네임 중복 확인
    public void nameCheck(String name){
        if(!userRepository.findByName(name).isEmpty())
            throw new GeneralException(ErrorStatus.NICKNAME_EXIST);
    }

    //이메일 변경
    public boolean changeMail(Long userId, String email){

        authService.isValidEmail(email);
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 이메일 변경
        user.setEmail(email);

        // 변경 사항 저장
        userRepository.save(user);
        return true;
    }

    //비밀번호 변경
    public void changePassword(String email, String password){

        authService.isValidEmail(email);
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        authService.validatePassword(password); // 비밀번호 검증

        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    //사용자 이메일 찾기
    public String findEmail(String name){
        //사용자 조회
        User user = userRepository.findByName(name)
                .orElseThrow(()-> new GeneralException((ErrorStatus.MEMBER_NOT_FOUND)));
        return user.getEmail();
    }

    //이미지 저장
    @Transactional
    public void saveImage(Long userId, String url){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new GeneralException((ErrorStatus.MEMBER_NOT_FOUND)));
        user.setImage(url);
        userRepository.save(user);
    }

    //회원탈퇴
    public void deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        userRepository.delete(user);
    }
}
