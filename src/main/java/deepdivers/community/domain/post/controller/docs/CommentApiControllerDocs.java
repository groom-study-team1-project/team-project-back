package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.post.dto.request.EditCommentRequest;
import deepdivers.community.domain.post.dto.request.LikeRequest;
import deepdivers.community.domain.post.dto.request.RemoveCommentRequest;
import deepdivers.community.domain.post.dto.request.WriteCommentRequest;
import deepdivers.community.domain.post.dto.request.WriteReplyRequest;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "08. 댓글 - 토큰", description = "댓글 관련 API")
public interface CommentApiControllerDocs {

    @Operation(summary = "댓글 작성", description = "게시글 댓글을 작성하는 기능")
    @ApiResponse(
        responseCode = "1400",
        description = """
                1. 댓글 작성에 성공하였습니다.
                """
    )
    @ApiResponse(
        responseCode = "2202, 2301 \n9000~9005",
        description = """
            1. 해당 게시글을 찾을 수 없습니다.
            2. 댓글은 100자 이하로 작성해주세요.
            3. 토큰 관련 예외입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> writeCommentOnPosts(Member member, WriteCommentRequest request);

    @Operation(summary = "답글 작성", description = "댓글에 답글을 작성하는 기능")
    @ApiResponse(
        responseCode = "1401",
        description = """
                    1. 답글 작성에 성공하였습니다.
                    """
    )
    @ApiResponse(
        responseCode = "2300, 2301\n9000~9005",
        description = """
            1. 댓글 정보가 없습니다.
            2. 댓글은 100자 이하로 작성해주세요.
            3. 토큰 관련 예외입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> writeReplyOnComment(Member member, WriteReplyRequest request);

    @Operation(summary = "댓글 및 답글 수정", description = "댓글 및 답글을 수정하는 기능")
    @ApiResponse(
        responseCode = "1402",
        description = """
                    1. 댓글 및 답글 수정에 성공하였습니다.
                    """
    )
    @ApiResponse(
        responseCode = "2300, 2301, 2302\n9000~9005",
        description = """
            1. 댓글 정보가 없습니다.
            2. 댓글은 100자 이하로 작성해주세요.
            3. 유효하지 않은 접근입니다.
            4. 토큰 관련 예외입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> updateComment(Member member, EditCommentRequest request);

    @Operation(summary = "댓글 및 답글 삭제", description = "댓글 및 답글을 삭제하는 기능")
    @ApiResponse(
        responseCode = "1403",
        description = """
                    1. 댓글 및 답글 삭제에 성공하였습니다.
                    """
    )
    @ApiResponse(
        responseCode = "2300, 2302\n9000~9005",
        description = """
            1. 댓글 정보가 없습니다.
            2. 유효하지 않은 접근입니다.
            3. 토큰 관련 예외입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> removeCommentOnPost(Member member, RemoveCommentRequest request);

    @Operation(summary = "댓글 및 답글 좋아요", description = "댓글 및 답글을 좋아요 하는 기능")
    @ApiResponse(
        responseCode = "1406",
        description = """
                    1. 댓글 좋아요에 성공했습니다.
                    """
    )
    @ApiResponse(
        responseCode = "2500\n9000~9005",
        description = """
            1. 유효하지 않은 접근입니다.
            2. 토큰 관련 예외입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> likeComment(Member member, LikeRequest request);

    @Operation(summary = "댓글 및 답글 좋아요 취소", description = "댓글 및 답글을 좋아요를 취소 하는 기능")
    @ApiResponse(
        responseCode = "1407",
        description = """
                    1. 댓글 좋아요 취소에 성공했습니다.
                    """
    )
    @ApiResponse(
        responseCode = "2500\n9000~9005",
        description = """
            1. 유효하지 않은 접근입니다.
            2. 토큰 관련 예외입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> unlikeComment(Member member, LikeRequest request);

}
