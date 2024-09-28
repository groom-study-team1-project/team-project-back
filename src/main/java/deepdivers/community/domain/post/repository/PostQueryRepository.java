package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;

import java.util.List;

public interface PostQueryRepository {

    List<AllMyPostsResponse> findAllMyPosts(final Long memberId, final Long lastTargetId, final Long categoryId);

    List<PostAllReadResponse> findAllPosts(Long lastContentId, Long categoryId);

}
