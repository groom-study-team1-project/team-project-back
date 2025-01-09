package deepdivers.community.domain.like.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "좋아요 요청")
public record LikeRequest (
    @Schema(description = "대상 ID", example = "1")
    @NotNull(message = "대상 정보가 필요합니다.")
    @NotNull Long targetId
) {
}
