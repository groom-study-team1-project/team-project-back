package deepdivers.community.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "게시글 수정 요청")
public record PostUpdateRequest(
	@Schema(description = "수정할 게시글 제목", example = "수정된 게시글 제목을 입력하세요.")
	@NotBlank(message = "게시글 제목은 필수입니다.")
	@Size(min = 2, max = 50, message = "게시글 제목은 최소 2자 이상, 최대 50자 이하이어야 합니다.")
	String title,

	@Schema(description = "수정할 게시글 내용", example = "수정된 게시글 내용을 입력하세요.")
	@NotBlank(message = "게시글 내용은 필수입니다.")
	@Size(min = 5, message = "게시글 내용은 최소 5자 이상이어야 합니다.")
	String content,

	@Schema(description = "수정할 카테고리 ID", example = "1")
	@NotNull(message = "카테고리 선택은 필수입니다.")
	Long categoryId,

	@Schema(description = "수정할 해시태그 목록", example = "[\"Java\", \"Spring\"]")
	String[] hashtags,

	@Schema(description = "수정할 게시글 이미지 파일")
	List<String> imageUrls
) {}
