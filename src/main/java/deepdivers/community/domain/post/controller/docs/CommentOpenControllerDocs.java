package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.dto.response.ContentResponse;
import deepdivers.community.domain.post.dto.response.GetCommentResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "7. 댓글", description = "댓글 조회 API")
public interface CommentOpenControllerDocs {

    @Schema(description = "댓글 조회")
    ResponseEntity<API<List<GetCommentResponse>>> getCommentsOnPost(
        @Schema(description = "부모 댓글 id", example = "1")
        Long commentId,
        @Schema(description = "조회자 id, default = 0 (비회원)", example = "0")
        Long memberId,
        @Schema(description = "마지막 조회 댓글 id, default = Long.MaxValue", example = "9223372000000000000")
        Long lastCommentId);

    @Schema(description = "답글 조회")
    ResponseEntity<API<List<ContentResponse>>> getRepliesOnComment(
        @Schema(description = "부모 댓글 id", example = "1")
        Long commentId,
        @Schema(description = "조회자 id, default = 0 (비회원)", example = "0")
        Long memberId,
        @Schema(description = "마지막 조회 댓글 id, default = Long.MaxValue", example = "9223372000000000000")
        Long lastCommentId);


}
