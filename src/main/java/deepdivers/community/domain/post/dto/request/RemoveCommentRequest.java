package deepdivers.community.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 삭제")
public record RemoveCommentRequest(
    @NotNull(message = "댓글 정보가 필요합니다.")
    @Schema(description = "댓글 ID", example = "1")
    Long commentId
) {
}
