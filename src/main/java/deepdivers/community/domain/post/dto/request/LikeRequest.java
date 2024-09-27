package deepdivers.community.domain.post.dto.request;

import deepdivers.community.domain.post.model.vo.LikeTarget;

public record LikeRequest (
    Long targetId
) {
}
