package deepdivers.community.domain.member.dto.response.result.type;

import deepdivers.community.domain.common.ResultType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SignUpResultType implements ResultType {

    MEMBER_SIGN_UP_SUCCESS(1000, "사용자 회원가입에 성공하였습니다.");

    private final Integer code;
    private final String message;

}
