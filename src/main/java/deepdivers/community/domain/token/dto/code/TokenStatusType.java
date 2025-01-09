package deepdivers.community.domain.token.dto.code;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenStatusType implements StatusType {

    RE_ISSUE_SUCCESS(8000, "토큰 재발급에 성공하였습니다.");

    private final int code;
    private final String message;

}
