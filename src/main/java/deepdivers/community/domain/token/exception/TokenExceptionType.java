package deepdivers.community.domain.token.exception;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenExceptionType implements ExceptionType {

    SIGNATURE_TOKEN(9000, "유효하지 않은 서명 정보입니다."),
    EXPIRED_TOKEN(9001, "토큰 유효기간이 만료되었습니다."),
    UNSUPPORTED_TOKEN(9002, "지원되지 않는 토큰 정보입니다."),
    MALFORMED_TOKEN(9003, "토큰 형식이 올바르지 않습니다."),
    UNKNOWN_TOKEN(9004, "알 수 없는 토큰 오류가 발생했습니다."),
    NOT_FOUND_TOKEN(9005, "토큰 정보를 찾을 수 없습니다.");


    private int code;
    private String message;

}
