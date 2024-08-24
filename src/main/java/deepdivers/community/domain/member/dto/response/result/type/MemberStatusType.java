package deepdivers.community.domain.member.dto.response.result.type;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatusType implements StatusType {

    MEMBER_SIGN_UP_SUCCESS(1000, "사용자 회원가입에 성공하였습니다."),
    MEMBER_LOGIN_SUCCESS(1001, "사용자 로그인에 성공하였습니다.");

    private final int code;
    private final String message;

}
