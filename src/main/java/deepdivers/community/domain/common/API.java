package deepdivers.community.domain.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 응답")
public record API<T>(
        @Schema(description = "응답 상태")
        StatusResponse status,
        @Schema(description = "응답 결과")
        T result
) {

    public static <T> API<T> of(final StatusType status, final T result) {
        return new API<>(StatusResponse.from(status), result);
    }

}