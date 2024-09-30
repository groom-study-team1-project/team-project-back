package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.global.security.jwt.Auth;
import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.request.PostDeleteRequest;
import deepdivers.community.domain.post.dto.request.PostUpdateRequest;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse;
import deepdivers.community.domain.post.dto.response.PostCountResponse;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.post.dto.response.PostUpdateResponse;
import deepdivers.community.domain.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "6. 게시글 - 토큰", description = "토큰 정보가 필요한 게시글 관련 API")
public interface PostApiControllerDocs {

	@Operation(summary = "게시글 작성", description = "새로운 게시글을 작성하는 기능")
	@ApiResponse(
		responseCode = "1201",
		description = "게시글 작성에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostCreateResponse.class))
	)
	ResponseEntity<API<PostCreateResponse>> createPost(Member member, PostCreateRequest request);

	@Operation(summary = "게시글 조회", description = "단일 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "게시글 조회에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostReadResponse.class))
	)
	ResponseEntity<API<PostReadResponse>> getPostById(Member member, Long postId);

	@Operation(summary = "전체 게시글 조회", description = "회원이 모든 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "모든 게시글 조회에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostReadResponse.class))
	)
	@ApiResponse(
		responseCode = "2202",
		description = "해당 게시글을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<PostCountResponse>> getAllPosts(Member member, Long categoryId, Long lastPostId);

	@Operation(summary = "게시글 수정", description = "기존 게시글을 수정하는 기능")
	@ApiResponse(
		responseCode = "1201",
		description = "게시글 수정에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostUpdateResponse.class))
	)
	ResponseEntity<API<PostUpdateResponse>> updatePost(Member member, Long postId, PostUpdateRequest request);

	@Operation(summary = "게시글 삭제", description = "게시글을 삭제하는 기능")
	@ApiResponse(
		responseCode = "1202",
		description = "게시글 삭제에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = NoContent.class))
	)
	ResponseEntity<NoContent> deletePost(Member member, Long postId);  // 삭제 메서드 추가
}

