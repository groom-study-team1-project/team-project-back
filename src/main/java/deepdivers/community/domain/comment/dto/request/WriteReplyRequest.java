package deepdivers.community.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 작성")
public record WriteReplyRequest(
    @Schema(description = "댓글 ID", example = "1")
    @NotNull(message = "댓글 정보가 필요합니다.")
    Long commentId,
    @Schema(description = "댓글 내용", example = "답글 작성입니당")
    @NotBlank(message = "댓글 내용이 필요합니다.")
    String content
) {
}
