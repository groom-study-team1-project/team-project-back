package deepdivers.community.domain.post.dto.request;

import deepdivers.community.domain.post.entity.PostSortType;

public record GetPostsRequest(
    Long categoryId,
    Long lastPostId,
    Integer lastCommentCount,
    Integer lastViewCount,
    PostSortType postSortType,
    Integer limit
) {
}
