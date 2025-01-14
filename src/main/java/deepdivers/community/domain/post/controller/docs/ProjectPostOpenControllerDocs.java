package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.ExceptionResponse;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "06. 프로젝트 게시글", description = "프로젝트 게시글 조회 API")
public interface ProjectPostOpenControllerDocs {

	@Operation(summary = "게시글 조회", description = "게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "게시글 조회에 성공하였습니다."
	)
	@ApiResponse(
		responseCode = "2202",
		description = "해당 게시글을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<ProjectPostDetailResponse>> getPostById(Long postId, Long viewerId);

	@Operation(summary = "모든 게시글 조회", description = "모든 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "모든 게시글 조회에 성공하였습니다."
	)
	@ApiResponse(
		responseCode = "2202",
		description = "해당 게시글을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<List<ProjectPostPreviewResponse>>> getAllPosts(GetPostsRequest request);

	@Operation(summary = "사용자가 작성한 게시글", description = "사용자가 작성한 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1009\n9000~9005",
		description = "사용자가 작성한 게시글 조회에 성공하였습니다."
	)
	@ApiResponse(
		responseCode = "9000~9005",
		description = "토큰 관련 예외입니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<List<ProjectPostPreviewResponse>>> getMyAllPosts(Long memberId, GetPostsRequest dto);

}
