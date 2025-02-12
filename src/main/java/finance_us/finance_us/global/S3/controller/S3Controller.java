package finance_us.finance_us.global.S3.controller;

import finance_us.finance_us.global.ApiResponse;
import finance_us.finance_us.global.S3.dto.S3Request;
import finance_us.finance_us.global.S3.dto.S3Response;
import finance_us.finance_us.global.S3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    // S3 이미지 업로드
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public ApiResponse<S3Response.S3ResponseDTO> uploadImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {

        S3Response.S3ResponseDTO response =  s3Service.saveS3(token, file);

        return ApiResponse.onSuccess(response); // 업로드된 파일 URL 반환

    }

    // 이미지 삭제
    @DeleteMapping(value = "/image")
    public ApiResponse<String> deleteImage(
            @RequestHeader("Authorization") String token, @RequestBody S3Request request){
        // 이전 이미지 삭제
        s3Service.deleteS3(token, request);

        return ApiResponse.onSuccess("삭제 완료되었습니다. ");
    }




}