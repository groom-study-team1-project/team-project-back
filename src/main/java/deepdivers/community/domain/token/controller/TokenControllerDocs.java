package deepdivers.community.domain.token.controller;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import deepdivers.community.global.security.jwt.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "토큰", description = "토큰 관련 API")
public interface TokenControllerDocs {

    @Operation(summary = "토큰 재발급", description = "토큰 재발급을 하는 기능")
    @ApiResponse(
            responseCode = "1000",
            description = """
                    1. 사용자 회원가입에 성공하였습니다.
                    """
    )
    @ApiResponse(
            responseCode = "2000, 2001, 2002, 2003, 2004, 2005",
            description = """
                    1. 사용자 비밀번호는 8글자부터 16글자로 영어 소문자, 특수문자, 숫자를 조합해주세요.
                    2. 사용자 닉네임은 2글자부터 최대 20자입니다.
                    3. 사용자 닉네임은 영어 대소문자와 한글 및 숫자의 조합으로 작성해주세요.
                    4. 이미 가입된 사용자 이메일입니다.
                    5. 이미 가입된 사용자 닉네임입니다.
                    6. 전화번호 형식을 맞춰주세요. ex) 010-0000-0000
                    """,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<TokenResponse> refreshToken(String refreshToken, Member member);

}
