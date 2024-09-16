package deepdivers.community.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountException {

    INVALID_EMAIL_FORMAT(2100, "이메일 형식을 맞춰주세요.");

    private final int code;
    private final String message;

}
