package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "05. 게시글", description = "비회원 게시글 조회 API")
public interface PostOpenControllerDocs {

	@Operation(summary = "게시글 조회 (비회원)", description = "비회원이 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "게시글 조회에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostDetailResponse.class))
	)
	@ApiResponse(
		responseCode = "2202",
		description = "해당 게시글을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<PostDetailResponse>> getPostById(Long postId, Long viewerId);

	@Operation(summary = "모든 게시글 조회 (비회원)", description = "비회원이 모든 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "모든 게시글 조회에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostPreviewResponse.class))
	)
	@ApiResponse(
		responseCode = "2202",
		description = "해당 게시글을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<List<PostPreviewResponse>>> getAllPosts(Long categoryId, Long lastPostId);

	@Operation(summary = "내가 작성한 게시글", description = "내가 작성한 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1009\n9000~9005",
		description = """
                    1. 내가 쓴 게시글 조회에 성공하였습니다.
                    """
	)
	@ApiResponse(
		responseCode = "9000~9005",
		description = """
                    1. 토큰 관련 예외입니다.
                    """,
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<List<PostPreviewResponse>>> getMyAllPosts(Long memberId, Long categoryId, Long lastPostId);

}
