package deepdivers.community.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "프로젝트 게시글 생성")
public record ProjectPostRequest(
    @Schema(description = "게시글 제목", example = "여기에 게시글 제목을 입력하세요.")
    @NotBlank(message = "게시글 제목은 필수입니다.")
    String title,

    @Schema(description = "게시글 내용", example = "여기에 게시글 내용을 작성하세요.")
    @NotBlank(message = "게시글 내용은 필수입니다.")
    String content,

    @Schema(description = "게시글 썸네일 URL", example = "posts/thumbnail.png")
    String thumbnailImageUrl,

    @Schema(description = "카테고리 ID", example = "1")
    @NotNull(message = "카테고리 선택은 필수입니다.")
    Long categoryId,

    @Schema(description = "해시태그 목록", example = "[\"Spring\", \"Boot\"]")
    @NotNull
    List<String> hashtags,

    @Schema(description = "게시글 이미지 키 목록", example = "[\"posts/uuid/image1.png\", \"posts/uuid/image2.png\"]")
    @NotNull
    List<String> imageKeys,

    @Schema(description = "슬라이드 이미지 키 값")
    @NotNull
    List<String> slideImageKeys
) {
}
