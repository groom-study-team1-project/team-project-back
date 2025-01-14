package deepdivers.community.domain.post.controller.interfaces;

import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import java.util.List;

public interface ProjectPostQueryRepository {

    List<ProjectPostPreviewResponse> findAllPosts(Long memberId, GetPostsRequest dto);

    ProjectPostDetailResponse readPostByPostId(Long postId, Long viewerId);

}
