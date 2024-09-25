package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import java.util.List;

public interface PostQueryRepository {

    List<AllMyPostsResponse> findAllMyPosts(final Long memberId, final Long lastTargetId, final Long categoryId);

}
