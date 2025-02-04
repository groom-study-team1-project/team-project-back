package deepdivers.community.domain.member.dto.code;

import deepdivers.community.domain.common.dto.code.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatusCode implements StatusCode {

    SEND_VERIFY_CODE_SUCCESS(1100, "이메일로 인증코드가 전송되었습니다."),
    VERIFY_EMAIL_SUCCESS(1101, "사용자 이메일 인증이 성공하였습니다."),
    NICKNAME_VALIDATE_SUCCESS(1102, "사용할 수 있는 닉네임입니다."),
    PASSWORD_CHANGE_SUCCESS(1103, "비밀번호 변경에 성공했습니다.");

    private final int code;
    private final String message;

}
