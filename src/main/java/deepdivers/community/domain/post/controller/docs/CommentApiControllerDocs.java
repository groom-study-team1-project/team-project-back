package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.WriteCommentRequest;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "댓글", description = "댓글 관련 API")
public interface CommentApiControllerDocs {

    @Operation(summary = "댓글 작성", description = "게시글 댓글을 작성하는 기능")
    @ApiResponse(
            responseCode = "1300",
            description = """
                    1. 댓글 작성에 성공하였습니다.
                    """
    )
    @ApiResponse(
        responseCode = "2202, \n9000~9005",
        description = """
            1. 해당 게시글을 찾을 수 없습니다.
            2. 토큰 관련 예외입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> writeCommentOnPosts(Member member, WriteCommentRequest request);

}
