package deepdivers.community.domain.post.dto.request;

import deepdivers.community.domain.post.entity.PostSortType;

public record PostSearchRequest(
    Long categoryId,
    String keyword,
    PostSortType sortType,
    Long lastPostId,
    Integer lastCommentCount,
    Integer lastViewCount,
    Integer limit
) {
}
