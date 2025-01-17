package finance_us.finance_us.global.code.status;


import finance_us.finance_us.global.code.BaseErrorCode;
import finance_us.finance_us.global.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4003", "이메일이 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    EMAIL_FAILED(HttpStatus.BAD_REQUEST, "MEMBER4004","이메일 전송에 실패하였습니다"),
    EMAIL_EXIST(HttpStatus.BAD_REQUEST,"MEMBER4005","이메일이 이미 존재합니다"),
    NICKNAME_EXIST(HttpStatus.BAD_REQUEST,"MEMBER4006","닉네임이 이미 존재합니다"),

    PASSWORD_VALIDATION_FAILED(HttpStatus.BAD_REQUEST,"PASSWORD4001","비밀번호는 영어 대/소문자, 숫자 중 2종류 이상을 조합해야 합니다."),
    EMAIL_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "MEMBER4005","올바르지 않은 이메일 형식입니다."),

    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "AUTH001", "JWT 서명이 올바르지 않습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH002", "JWT 토큰이 만료되었습니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "AUTH003", "JWT 토큰이 올바르지 않은 형식입니다."),


    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_MISSION_4001", "미션이 존재하지 않습니다."),

    MEMBER_MISSION_ALREADY_IN_CHALLENGING(HttpStatus.CONFLICT, "MEMBER_MISSION4002", "이미 진행 중인 미션입니다."),
    MEMBER_MISSION_NOT_CHALLENGING(HttpStatus.BAD_REQUEST, "MEMBERMISSION4003", "회원이 도전 중인 미션이 아닙니다."),

    FOOD_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "FOOD_CATEGORY4001", "음식 카테고리가 없습니다."),

    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "RESTAURANT4001", "식당이 없습니다."),

    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "REGION4001", "지역이 없습니다."),

    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다."),

    PAGE_BOUND_ERROR(HttpStatus.BAD_REQUEST, "PAGE4001", "페이징 번호가 적절하지 않습니다."),


    CATEGORY_TYPE_ERROR(HttpStatus.BAD_REQUEST,"CATEGORY4001", "카테고리의 타입 문자열이 잘못되었습니다."),
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트");







    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
