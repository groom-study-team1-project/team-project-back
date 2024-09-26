package deepdivers.community.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WriteCommentRequest(
    @NotNull(message = "게시글 정보가 필요합니다.")
    Long postId,
    @NotBlank(message = "댓글 내용이 필요합니다.")
    String content
) {
}
