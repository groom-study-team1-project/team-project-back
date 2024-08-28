package deepdivers.community.domain.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "응답 상태")
public record StatusResponse (
        @Schema(description = "응답 코드", example = "9999")
        int code,
        @Schema(description = "응답 코드", example = "응답 성공 메시지입니다.")
        String message
) {

    public static StatusResponse from(final StatusType statusType) {
        return new StatusResponse(statusType.getCode(), statusType.getMessage());
    }

}
