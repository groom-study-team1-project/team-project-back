package deepdivers.community.domain.post.dto.request;

import jakarta.validation.constraints.NotNull;

public record LikeRequest (
    @NotNull Long targetId
) {
}
