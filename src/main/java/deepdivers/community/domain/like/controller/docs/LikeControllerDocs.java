package deepdivers.community.domain.like.controller.docs;

import deepdivers.community.domain.common.dto.response.ExceptionResponse;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.like.dto.LikeRequest;
import deepdivers.community.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface LikeControllerDocs {

    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요를 하는 기능")
    @ApiResponse(
        responseCode = "1205",
        description = " 게시글 좋아요에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "2500\n9000~9005",
        description = """
            1. 유효하지 않은 접근입니다.
            2. 토큰 관련 예외입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> likePost(Member member, LikeRequest request);

    @Operation(summary = "게시글 좋아요 취소", description = "게시글 좋아요를 취소 하는 기능")
    @ApiResponse(
        responseCode = "1206",
        description = "게시글 좋아요 취소에 성공했습니다."
    )
    @ApiResponse(
        responseCode = "2500\n9000~9005",
        description = """
            1. 유효하지 않은 접근입니다.
            2. 토큰 관련 예외입니다.
            """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> unlikePost(Member member, LikeRequest request);

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
