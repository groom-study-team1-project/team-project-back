package deepdivers.community.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 삭제")
public record RemoveCommentRequest(
    @NotNull
    @Schema(description = "댓글 ID", example = "1")
    Long commentId
) {
}
