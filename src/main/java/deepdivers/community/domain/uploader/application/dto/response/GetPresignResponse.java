package deepdivers.community.domain.uploader.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Presigned URL 응답")
public record GetPresignResponse(
    @Schema(description = "파일에 대한 key값", example = "/bucket/file.extension")
    String fileKey,
    @Schema(description = "서명된 S3 업로드 Url", example = "https://host/bucket/file.extension?presignInfo")
    String presignedUrl,
    @Schema(description = "파일에 대한 imageKey", example = "https://host/bucket/file.extension")
    String accessUrl
) {

    public static GetPresignResponse of(
        final String key,
        final String presignedUrl,
        final String accessUrl
    ) {
        return new GetPresignResponse(key, presignedUrl, accessUrl);
    }

}
