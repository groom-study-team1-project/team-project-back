package deepdivers.community.domain.member.dto.response.statustype;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatusType implements StatusType {

    VERIFY_EMAIL_SUCCESS(1100, "사용자 이메일 인증이 성공하였습니다.");

    private final int code;
    private final String message;

}
