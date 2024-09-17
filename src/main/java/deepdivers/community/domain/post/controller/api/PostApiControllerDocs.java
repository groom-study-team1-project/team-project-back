package deepdivers.community.domain.post.controller.api;

import org.springframework.http.ResponseEntity;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.post.dto.request.PostCreateRequest;
import deepdivers.community.domain.post.dto.response.PostCreateResponse;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "게시글", description = "게시글 관련 API")
public interface PostApiControllerDocs {

	@Operation(summary = "게시글 작성", description = "새로운 게시글을 작성하는 기능")
	@ApiResponse(
		responseCode = "1201",
		description = """
                    게시글 작성에 성공하였습니다.
                    """,
		content = @Content(schema = @Schema(implementation = PostCreateResponse.class))
	)
	@ApiResponse(
		responseCode = "2200, 2201, 3200, \n9000~9005",
		description = """
                    1. 게시글 제목은 2자 이상, 50자 이하로 작성해야 합니다.
                    2. 게시글 내용은 최소 5자 이상이어야 합니다.
                    3. 해당 카테고리를 찾을 수 없습니다.
                    4. 토큰 관련 예외입니다.
                    """,
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ResponseEntity<API<PostCreateResponse>> createPost(Member member, PostCreateRequest request);
}
