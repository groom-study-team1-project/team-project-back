package deepdivers.community.global.exception.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "요청 검증 에러 응답")
public record ErrorResponse(
        @Schema(description = "검증 실패 필드", example = "필드 네임")
        String fieldName,

        @Schema(description = "검증 실패 에러 메시지", example = "필드 타입이 잘못되었습니다.")
        String message
) {

    @Override
    public String toString() {
        return "{" +
                "fieldName='" + fieldName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}