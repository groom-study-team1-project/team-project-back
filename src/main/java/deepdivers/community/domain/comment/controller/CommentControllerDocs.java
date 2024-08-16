package deepdivers.community.domain.comment.controller;

import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "댓글", description = "댓글 관련 API")
public interface CommentControllerDocs {

    @Operation(summary = "댓글 작성", description = "게시글 댓글을 작성하는 기능")
    @ApiResponse(
            responseCode = "200",
            description = """
                    성공
                    """
    )
    @ApiResponse(
            responseCode = "400",
            description = """
                    1. Request의 변수 정보가 ...
                    """,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = """
                    1. 존재하지 않는 ...
                    """,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    void example();

}
