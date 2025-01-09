package deepdivers.community.domain.token.dto.response;

import deepdivers.community.domain.common.dto.response.StatusResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 응답")
public record ReissueResponse(
        @Schema(description = "토큰 재발급 상태")
        StatusResponse status,
        TokenResponse result
) {
}
