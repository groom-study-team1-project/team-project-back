package deepdivers.community.domain.member.exception;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountException implements ExceptionType {

    INVALID_EMAIL_FORMAT(2100, "이메일 형식을 맞춰주세요."),
    INVALID_PASSWORD(2101, "비밀번호가 틀렸습니다.");

    private final int code;
    private final String message;

}
