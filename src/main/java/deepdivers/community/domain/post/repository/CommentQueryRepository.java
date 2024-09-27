package deepdivers.community.domain.post.repository;

import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import deepdivers.community.domain.post.dto.response.CommentResponse;
import deepdivers.community.domain.post.dto.response.GetCommentResponse;
import java.util.List;

public interface CommentQueryRepository {

    List<GetCommentResponse> findTop5CommentsByPost(Long postId, Long memberId, Long lastCommentId);

}
