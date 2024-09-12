package deepdivers.community.domain.mail.dto.statustype;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailStatus implements StatusType {

    SUCCESS(6000, "이메일 전송이 성공했습니다.");

    private final int code;
    private final String message;

}
