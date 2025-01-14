package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.like.dto.LikeRequest;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.request.ProjectPostRequest;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.domain.common.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "06. 게시글 - 토큰", description = "토큰 정보가 필요한 게시글 관련 API")
public interface PostApiControllerDocs {

	@Operation(summary = "게시글 작성", description = "새로운 게시글을 작성하는 기능")
	@ApiResponse(
		responseCode = "1201",
		description = "게시글 작성에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostSaveResponse.class))
	)
	ResponseEntity<API<PostSaveResponse>> createPost(Member member, PostSaveRequest request);

	@Operation(summary = "게시글 수정", description = "기존 게시글을 수정하는 기능")
	@ApiResponse(
		responseCode = "1201",
		description = "게시글 수정에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostSaveResponse.class))
	)
	ResponseEntity<API<PostSaveResponse>> updatePost(Member member, Long postId, PostSaveRequest request);

	@Operation(summary = "게시글 삭제", description = "게시글을 삭제하는 기능")
	@ApiResponse(
		responseCode = "1202",
		description = "게시글 삭제에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = NoContent.class))
	)
	ResponseEntity<NoContent> deletePost(Member member, Long postId);

	@Operation(summary = "프로젝트 게시글 작성", description = "프로젝트 게시글을 작성하는 기능")
	@ApiResponse(
		responseCode = "1205",
		description = "프로젝트 게시글 작성에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostSaveResponse.class))
	)
	ResponseEntity<API<Long>> createProject(Member member, ProjectPostRequest dto);

	@Operation(summary = "프로젝트 게시글 수정", description = "프로젝트 게시글을 수정하는 기능")
	@ApiResponse(
		responseCode = "1206",
		description = "프로젝트 게시글 수정에 성공하였습니다."
	)
	ResponseEntity<API<Long>> editProject(Member member, ProjectPostRequest dto, Long projectId);

	@Operation(summary = "프로젝트 게시글 삭제", description = "프로젝트 게시글을 삭제하는 기능")
	@ApiResponse(
		responseCode = "1207",
		description = "프로젝트 게시글 삭제에 성공하였습니다."
	)
	ResponseEntity<NoContent> removeProject(Member member, Long projectId);

}

