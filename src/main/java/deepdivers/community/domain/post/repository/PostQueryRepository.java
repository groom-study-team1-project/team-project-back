package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import deepdivers.community.domain.post.dto.response.GetAllPostsResponse;

import java.util.List;

public interface PostQueryRepository {

    List<AllMyPostsResponse> findAllMyPosts(Long memberId, Long lastTargetId, Long categoryId);

    List<GetAllPostsResponse> findAllPosts(Long lastContentId, Long categoryId);

}
