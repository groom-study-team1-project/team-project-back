package deepdivers.community.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로필 이미지 업로드 응답")
public record ImageUploadResponse(
        @Schema(description = "업로드된 이미지 URL", example = "http://localhost:4566/test-bucket/profiles/11/2b776b15_1725181775362.jpeg")
        String imageUrl
) {

        public static ImageUploadResponse of(final String imageUrl) {
                return new ImageUploadResponse(imageUrl);
        }

}