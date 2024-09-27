package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.post.dto.response.ContentResponse;
import deepdivers.community.domain.post.dto.response.GetCommentResponse;
import java.util.List;

public interface CommentQueryRepository {

    List<GetCommentResponse> findTop5CommentsByPost(Long postId, Long memberId, Long lastCommentId);
    List<ContentResponse> findTop5RepliesByComment(Long commentId, Long memberId, Long lastCommentId);

}
