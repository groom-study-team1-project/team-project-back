package deepdivers.community.domain.category.controller.docs;

import deepdivers.community.domain.category.dto.response.CategoryResponse;
import deepdivers.community.domain.category.dto.response.MemberPostCountByCategoryResponse;
import deepdivers.community.domain.common.dto.response.API;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "09. 카테고리", description = "카테고리 관련 API")
public interface CategoryOpenControllerDocs {

	@Operation(summary = "카테고리 목록 조회", description = "모든 카테고리의 ID, 제목, 설명을 조회하는 API입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 카테고리 목록을 반환했습니다.",
			content = @Content(schema = @Schema(implementation = API.class))),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.", content = @Content)
	})
	ResponseEntity<API<List<CategoryResponse>>> getAllCategories();

	ResponseEntity<API<List<MemberPostCountByCategoryResponse>>> getMemberPostCountByCategory(Long memberId);

}
