package deepdivers.community.domain.image.ui.docs;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.image.application.dto.request.GetPresignRequest;
import deepdivers.community.domain.image.application.dto.response.GetPresignResponse;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "10. 업로더", description = "업로더 관련 API")
public interface UploaderOpenControllerDocs {

    @Operation(summary = "서명된 S3 업로드 URL 생성", description = "S3 업로드를 위한 URL을 생성합니다.")
    @ApiResponse(responseCode = "4000", description = "서명된 URL을 성공적으로 생성했습니다.")
    @ApiResponse(
        responseCode = "9101",
        description = "이미지 파일 형식이 아닙니다.",
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<API<GetPresignResponse>> generatePresignUrl(GetPresignRequest dto);

}
