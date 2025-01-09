package deepdivers.community.domain.common.dto.response;


import deepdivers.community.domain.common.dto.code.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 응답")
public record NoContent(
        @Schema(description = "응답 상태")
        StatusResponse status
) {

    public static NoContent from(final StatusCode status) {
        return new NoContent(StatusResponse.from(status));
    }

}