package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

@Tag(name = "게시글", description = "비회원 게시글 조회 API")
public interface PostOpenControllerDocs {

	@Operation(summary = "게시글 조회 (비회원)", description = "비회원이 게시글을 조회하는 기능")
	@ApiResponse(
		responseCode = "1203",
		description = "게시글 조회에 성공하였습니다.",
		content = @Content(schema = @Schema(implementation = PostReadResponse.class))
	)
	@ApiResponse(
		responseCode = "2202",
		description = "해당 게시글을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<PostReadResponse>> getPostById(Long postId, HttpServletRequest request);  // HttpServletRequest 추가
}
