package deepdivers.community.domain.token.controller;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.token.dto.ReissueResponse;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import deepdivers.community.global.security.jwt.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "토큰", description = "토큰 관련 API")
public interface TokenControllerDocs {

    @Operation(
            summary = "토큰 재발급",
            description = "토큰 재발급을 하는 기능"
    )
    @ApiResponse(
            responseCode = "8000",
            description = """
                    1. 토큰 재발급에 성공하였습니다.
                    """
    )
    @ApiResponse(
            responseCode = "9000, 9001, 9002, 9003, 9004, 9005",
            description = """
                    1. 유효하지 않은 서명 정보입니다.
                    2. 토큰 유효기간이 만료되었습니다.
                    3. 지원되지 않는 토큰 정보입니다.
                    4. 토큰 형식이 올바르지 않습니다.
                    5. 알 수 없는 토큰 오류가 발생했습니다.
                    6. 토큰 정보를 찾을 수 없습니다.
                    """,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<API<TokenResponse>> reIssue(String accessToken, String refreshToken);

}
