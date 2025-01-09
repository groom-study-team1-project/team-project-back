package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import java.util.List;

public interface PostQueryRepository {

    List<PostPreviewResponse> findAllPosts(Long memberId, GetPostsRequest dto);

    PostDetailResponse readPostByPostId(Long postId, Long viewerId);

}
