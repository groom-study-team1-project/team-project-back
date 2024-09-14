package deepdivers.community.domain.member.controller.docs;

import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.AuthenticateEmailRequest;
import deepdivers.community.domain.member.dto.request.VerifyEmailRequest;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "3. 회원 계정", description = "회원 계정 관련 API")
public interface AccountOpenControllerDocs {

    @Operation(summary = "닉네임", description = "닉네임 검사를 하는 기능")
    @ApiResponse(
        responseCode = "1005",
        description = """
                    1. 사용할 수 있는 닉네임입니다.
                    """
    )
    @ApiResponse(
        responseCode = "2001, 2002, 2004",
        description = """
                    1. 사용자 닉네임은 2글자부터 최대 20자입니다.
                    2. 사용자 닉네임은 영어 대소문자와 한글 및 숫자의 조합으로 작성해주세요.
                    3. 이미 가입된 사용자 닉네임입니다.
                    """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> verifyNickname(String nickname);

    @Operation(summary = "이메일", description = "이메일 인증을 하는 기능")
    @ApiResponse(
        responseCode = "1100",
        description = """
                    1. 이메일로 인증코드가 전송되었습니다.
                    """
    )
    @ApiResponse(
        responseCode = "2003, 2010",
        description = """
                    1. 이미 가입된 사용자 이메일입니다.
                    2. 이메일 형식을 맞춰주세요.
                    """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> sendEmail(AuthenticateEmailRequest request);

}
