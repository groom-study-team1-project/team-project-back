package deepdivers.community.domain.token.dto;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.common.StatusType;

public record ReissueResponse(
        StatusResponse status,
        TokenResponse result
) {
}
