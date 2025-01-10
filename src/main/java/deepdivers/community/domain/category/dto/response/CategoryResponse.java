package deepdivers.community.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

	@Schema(description = "카테고리 ID", example = "1")
	private Long id;

	@Schema(description = "카테고리 제목", example = "Technology")
	String title;

	@Schema(description = "카테고리 설명", example = "Posts about technology")
	String description;

}