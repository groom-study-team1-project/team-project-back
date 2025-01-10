package deepdivers.community.domain.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(
	@Schema(description = "카테고리 ID", example = "1")
	Long id,

	@Schema(description = "카테고리 제목", example = "Technology")
	String title,

	@Schema(description = "카테고리 설명", example = "Posts about technology")
	String description
) {
}
