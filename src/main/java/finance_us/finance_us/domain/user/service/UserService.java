package finance_us.finance_us.domain.user.service;

import finance_us.finance_us.domain.user.converter.AuthConverter;
import finance_us.finance_us.domain.user.dto.AuthRequestDTO;
import finance_us.finance_us.domain.user.dto.AuthResponseDTO;
import finance_us.finance_us.domain.user.entity.User;
import finance_us.finance_us.domain.user.repository.UserRepository;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.global.file.S3FileService;
import finance_us.finance_us.security.TokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final S3FileService s3FileService;
    @Lazy
    private final TokenProvider tokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //이메일 중복 확인
    public void mailCheck(String email) {
        authService.isValidEmail(email);
        if(userRepository.findByEmail(email).isEmpty())
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
    }

    //이메일 중복 확인2
    public void mailCheck2(String email) {
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
    public long changeMail(String token, String email){
        Long userId = tokenProvider.extractUserIdFromToken(token);
        authService.isValidEmail(email);
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 이메일 변경
        user.setEmail(email);

        // 변경 사항 저장
        userRepository.save(user);
        return userId;
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

    //비밀번호 확인
    public boolean passwordCheck(String token, String password){
        Long userId = tokenProvider.extractUserIdFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        if (!passwordEncoder.matches(password, user.getPassword())) return false;

        return true;
    }


    //이미지 저장
    @Transactional
    public String saveImage(String token, String imageUrl, String imageName) {

        Long userId = tokenProvider.extractUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException((ErrorStatus.MEMBER_NOT_FOUND)));
        user.setImageName(imageName);
        user.setImage(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    //이미지 삭제
    @Transactional
    public void deleteImage(String token){
        Long userId = tokenProvider.extractUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException((ErrorStatus.MEMBER_NOT_FOUND)));

        user.setImage(null);
        user.setImageName(null);
        userRepository.save(user);

    }

    // 유저 읽어오기
    public AuthResponseDTO.ReadResponseDTO readUser(String token)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException((ErrorStatus.MEMBER_NOT_FOUND)));

        return AuthConverter.toReadResponseDTO(user);
    }

    // 유저 수정
    public String updateUser(String token, AuthRequestDTO.UpdateRequestDTO dto)
    {
        Long userId = tokenProvider.extractUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException((ErrorStatus.MEMBER_NOT_FOUND)));

        user.setName(dto.getName());
        user.setAge(dto.getAgeGroup());
        user.setOne_liner(dto.getOne_liner());
        user.setJob(dto.getJobCategory());

        userRepository.save(user);

        return "success";

    }

    //회원탈퇴
    public void deleteUser(String token){
        Long userId = tokenProvider.extractUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        user.setName("탈퇴한 사용자입니다.");
        user.setOne_liner(null);
user.setEmail(null);
     user.setPassword(null); 
user.setImageName(null);
        user.setImage(null);

        userRepository.save(user);

    }


}
