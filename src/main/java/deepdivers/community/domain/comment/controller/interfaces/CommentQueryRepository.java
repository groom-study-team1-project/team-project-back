package deepdivers.community.domain.comment.controller.interfaces;

import deepdivers.community.domain.comment.dto.response.ContentResponse;
import deepdivers.community.domain.comment.dto.response.GetCommentResponse;
import java.util.List;

public interface CommentQueryRepository {

    List<GetCommentResponse> findTop5CommentsByPost(Long postId, Long memberId, Long lastCommentId);
    List<ContentResponse> findTop5RepliesByComment(Long commentId, Long memberId, Long lastCommentId);

}
