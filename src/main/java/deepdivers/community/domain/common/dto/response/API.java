package deepdivers.community.domain.common.dto.response;

import deepdivers.community.domain.common.dto.code.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 응답")
public record API<T>(
        @Schema(description = "응답 상태")
        StatusResponse status,
        @Schema(description = "응답 결과")
        T result
) {

    public static <T> API<T> of(final StatusCode status, final T result) {
        return new API<>(StatusResponse.from(status), result);
    }

}
