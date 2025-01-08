package deepdivers.community.domain.image.application.dto.response.statustype;

import deepdivers.community.domain.common.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploaderStatusType implements StatusType {

    GENERATE_PRESIGN_SUCCESS(4000, "서명된 URL을 성공적으로 생성했습니다.");

    private final int code;
    private final String message;

}