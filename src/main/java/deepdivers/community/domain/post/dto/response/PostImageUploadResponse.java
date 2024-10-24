package deepdivers.community.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 이미지 업로드 응답")
public record PostImageUploadResponse(
        @Schema(description = "업로드된 게시글 이미지 URL", example = "http://localhost:4566/test-bucket/profiles/11/2b776b15_1725181775362.jpeg")
        String imageUrl
) {

    public static PostImageUploadResponse of(final String imageUrl) {
        return new PostImageUploadResponse(imageUrl);
    }

}