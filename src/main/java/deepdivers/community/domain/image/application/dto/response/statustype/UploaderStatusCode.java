package deepdivers.community.domain.image.application.dto.response.statustype;

import deepdivers.community.domain.common.dto.code.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploaderStatusCode implements StatusCode {

    GENERATE_PRESIGN_SUCCESS(4000, "서명된 URL을 성공적으로 생성했습니다.");

    private final int code;
    private final String message;

}