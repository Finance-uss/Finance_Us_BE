package finance_us.finance_us.global.S3.service;

import finance_us.finance_us.global.S3.dto.S3Response;
import finance_us.finance_us.global.code.status.ErrorStatus;
import finance_us.finance_us.global.exception.GeneralException;
import finance_us.finance_us.global.file.S3FileService;
import finance_us.finance_us.security.TokenProvider;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class S3Service {
    private final S3FileService s3FileService;
    private final TokenProvider tokenProvider;

    //S3 이미지 업로드
    @Transactional
    public S3Response.S3ResponseDTO saveS3(String token, MultipartFile file) {

        tokenProvider.extractUserIdFromToken(token);
        String imageUrl;
        String imageName = UUID.randomUUID().toString() + "_" +file.getOriginalFilename();
        try {
            // S3에 이미지 업로드
            imageUrl = s3FileService.saveFile(file);
        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.IMAGE_FAILED);
        }

        return new S3Response.S3ResponseDTO(imageUrl, imageName);
    }
}

