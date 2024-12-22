package deepdivers.community.domain.uploader.application.dto.request;

import deepdivers.community.infra.aws.s3.KeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Presigned URL 요청")
public record GetPresignRequest(
    @NotBlank(message = "업로드 할 파일의 contentType은 필수입니다.")
    @Schema(description = "업로드 할 파일의 contentType", example = "image/jpeg")
    String contentType,
    @NotNull(message = "업로드 할 파일의 KeyType은 필수입니다.")
    @Schema(description = "업로드 할 파일의 keyType", example = "POST, PROFILE")
    KeyType keyType
) {
}
