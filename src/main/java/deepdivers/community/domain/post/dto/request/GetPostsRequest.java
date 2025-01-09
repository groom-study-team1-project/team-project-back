package deepdivers.community.domain.post.dto.request;

import deepdivers.community.domain.post.entity.PostSortType;

public record GetPostsRequest(
    Long categoryId,
    Long lastPostId,
    PostSortType postSortType,
    Integer limit
) {
}
