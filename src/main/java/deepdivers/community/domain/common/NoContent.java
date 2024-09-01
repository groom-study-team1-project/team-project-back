package deepdivers.community.domain.common;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 응답")
public record NoContent(
        @Schema(description = "응답 상태")
        StatusResponse status
) {

    public static NoContent from(final StatusType status) {
        return new NoContent(StatusResponse.from(status));
    }

}