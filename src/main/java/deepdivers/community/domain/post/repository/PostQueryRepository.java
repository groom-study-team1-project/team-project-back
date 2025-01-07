package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import java.util.List;

public interface PostQueryRepository {

    List<PostPreviewResponse> findAllPosts(Long memberId, Long lastContentId, Long categoryId);

    PostDetailResponse readPostByPostId(Long postId, Long viewerId);

}
