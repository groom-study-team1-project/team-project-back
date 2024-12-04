package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostSaveRequest;
import deepdivers.community.domain.post.dto.response.PostImageUploadResponse;
import deepdivers.community.domain.post.dto.response.PostSaveResponse;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "6. 게시글 - 토큰", description = "토큰 정보가 필요한 게시글 관련 API")
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

	@Operation(summary = "게시글 이미지 업로드", description = "게시글 이미지를 업로드하는 기능")
	@ApiResponse(
		responseCode = "1204",
		description = """
				1. 게시글 이미지 업로드에 성공하였습니다.
				"""
	)
	@ApiResponse(
		responseCode = "4002",
		description = "유효하지 않은 이미지 파일입니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "9000~9005",
		description = """
				1. 토큰 관련 예외입니다.
				""",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<PostImageUploadResponse>> uploadPostImage(MultipartFile imageFile);
}

