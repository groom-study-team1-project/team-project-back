package deepdivers.community.domain.comment.controller.docs;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.comment.dto.response.ContentResponse;
import deepdivers.community.domain.comment.dto.response.GetCommentResponse;
import deepdivers.community.domain.common.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "07. 댓글", description = "댓글 조회 API")
public interface CommentOpenControllerDocs {

    @Operation(summary = "댓글 조회", description = "게시글 댓글을 조회하는 기능")
    @ApiResponse(
        responseCode = "1404",
        description = """
                    1. 댓글 조회에 성공하였습니다.
                    """
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<API<List<GetCommentResponse>>> getCommentsOnPost(
        @Schema(description = "게시글 id", example = "1")
        Long commentId,
        @Schema(description = "마지막 조회 댓글 id", example = "99999")
        Long lastCommentId,
        Long viewerId
    );

    @Operation(summary = "답글 조회", description = "게시글 댓글의 답글을 조회하는 기능")
    @ApiResponse(
        responseCode = "1405",
        description = """
                    1. 답글 조회에 성공하였습니다.
                    """
    )
    @ApiResponse(content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<API<List<ContentResponse>>> getRepliesOnComment(
        @Schema(description = "부모 댓글 id", example = "1")
        Long commentId,
        @Schema(description = "마지막 조회 댓글 id", example = "99999")
        Long lastCommentId,
        Long viewerId
    );

}
