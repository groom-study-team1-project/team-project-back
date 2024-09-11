package deepdivers.community.domain.email.dto.statustype;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum EmailStatus implements StatusType {

    SUCCESS(6000, "이메일 전송이 성공했습니다.");

    private final int code;
    private final String message;

}
