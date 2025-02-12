package finance_us.finance_us.domain.account.service;

import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.global.file.S3FileService;
import finance_us.finance_us.security.TokenProvider;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
public class AccountImageService {
    private final S3FileService s3FileService;
    private final TokenProvider tokenProvider;

    //S3 이미지 업로드
    @Transactional
    public String saveImage(String token, MultipartFile file) {

        tokenProvider.extractUserIdFromToken(token);
        String imageUrl;
        try {
            // S3에 이미지 업로드
            imageUrl = s3FileService.saveFile(file);
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.IMAGE_FAILED);
        }

        return imageUrl;
    }
}
