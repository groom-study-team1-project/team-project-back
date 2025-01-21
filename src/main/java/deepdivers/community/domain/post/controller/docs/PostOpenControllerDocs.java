package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.post.dto.request.GetPostsRequest;
import deepdivers.community.domain.post.dto.response.NormalPostPageResponse;
import deepdivers.community.domain.post.dto.response.PostDetailResponse;
import deepdivers.community.domain.post.dto.response.PostPreviewResponse;
import deepdivers.community.domain.common.dto.response.ExceptionResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostDetailResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPageResponse;
import deepdivers.community.domain.post.dto.response.ProjectPostPreviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "05. 게시글", description = "비회원 게시글 조회 API")
public interface PostOpenControllerDocs {

	@Operation(summary = "일반 게시글 상세 조회", description = "일반 게시글을 상세 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "게시글 조회에 성공하였습니다."
	)
	ResponseEntity<API<PostDetailResponse>> getPostById(Long postId, Long viewerId);

	@Operation(summary = "모든 일반 게시글 조회", description = "모든 일반 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "모든 게시글 조회에 성공하였습니다."
	)
	ResponseEntity<API<List<PostPreviewResponse>>> getAllPosts(GetPostsRequest request);

	@Operation(summary = "사용자가 작성한 일반 게시글 목록 조회", description = "사용자가 작성한 일반 게시글 목록을 조회하는 기능")
	@ApiResponse(
		responseCode = "1009",
		description = "사용자가 작성한 게시글 조회에 성공하였습니다."
	)
	ResponseEntity<API<List<PostPreviewResponse>>> getAllPostsByMember(Long memberId, GetPostsRequest dto);

	@Operation(summary = "프로젝트 게시글 상세 조회", description = "프로젝트 게시글을 상세 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "게시글 조회에 성공하였습니다."
	)
	ResponseEntity<API<ProjectPostDetailResponse>> getProjectPostById(Long postId, Long viewerId);

	@Operation(summary = "모든 프로젝트 게시글 조회", description = "모든 프로젝트 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "모든 게시글 조회에 성공하였습니다."
	)
	ResponseEntity<API<List<ProjectPostPreviewResponse>>> getAllProjectPosts(GetPostsRequest request);

	@Operation(summary = "사용자가 작성한 프로젝트 게시글 목록 조회 ", description = "사용자가 작성한 프로젝트 게시글 목록을 조회하는 기능")
	@ApiResponse(
		responseCode = "1009",
		description = "사용자가 작성한 게시글 조회에 성공하였습니다."
	)
	ResponseEntity<API<List<ProjectPostPreviewResponse>>> getAllProjectPostsByMember(Long memberId, GetPostsRequest dto);

	@Operation(summary = "일반 게시글 페이지 API", description = "일반 게시글 페이지 API 응답")
	@ApiResponse(
		responseCode = "1009",
		description = "사용자가 작성한 게시글 조회에 성공하였습니다."
	)
	ResponseEntity<API<NormalPostPageResponse>> getNormalPostApi(GetPostsRequest dto);

	@Operation(summary = "프로젝트 게시글 페이지 API", description = "일반 게시글 페이지 API 응답")
	@ApiResponse(
		responseCode = "1009",
		description = "사용자가 작성한 게시글 조회에 성공하였습니다."
	)
	ResponseEntity<API<ProjectPostPageResponse>> getProjectPostApi(GetPostsRequest request);
}
