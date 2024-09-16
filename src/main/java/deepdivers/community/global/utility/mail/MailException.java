package deepdivers.community.global.utility.mail;

import deepdivers.community.global.exception.model.ExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailException implements ExceptionType {

    INVALID_VERIFY_CODE(9200, "유효하지 않은 인증코드입니다."),
    NOT_SENT_VERIFY_CODE(9201, "인증 코드가 전송되지 않은 이메일입니다.");

    private final int code;
    private final String message;

}
