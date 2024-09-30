package deepdivers.community.domain.post.controller.docs;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.response.PostAllReadResponse;
import deepdivers.community.domain.post.dto.response.PostCountResponse;
import deepdivers.community.domain.post.dto.response.PostReadResponse;
import deepdivers.community.domain.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "5. 게시글", description = "비회원 게시글 조회 API")
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
	ResponseEntity<API<PostReadResponse>> getPostById(Long postId, HttpServletRequest request);

	@Operation(summary = "모든 게시글 조회 (비회원)", description = "비회원이 모든 게시글을 조회하는 기능")
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
	ResponseEntity<API<PostCountResponse>> getAllPosts(Long categoryId, Long lastPostId);
}
