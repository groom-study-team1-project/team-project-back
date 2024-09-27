package deepdivers.community.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "게시글 삭제 요청")
public record PostDeleteRequest(
	@Schema(description = "삭제할 게시글 ID", example = "1")
	@NotNull(message = "게시글 ID는 필수입니다.")
	Long postId
) {}
