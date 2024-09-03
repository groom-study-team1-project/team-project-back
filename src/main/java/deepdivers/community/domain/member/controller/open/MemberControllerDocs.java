package deepdivers.community.domain.member.controller.open;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.MemberLoginRequest;
import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "사용자", description = "사용자 관련 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원가입", description = "회원가입을 하는 기능")
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
    ResponseEntity<NoContent> signUp(MemberSignUpRequest request);

    @Operation(summary = "로그인", description = "로그인을 하는 기능")
    @ApiResponse(
            responseCode = "1001",
            description = """
                    1. 사용자 로그인에 성공하였습니다.
                    """
    )
    @ApiResponse(
            responseCode = "2006, 2007, 2008, 2009",
            description = """
                    1. 존재하지 않는 이메일 계정입니다.
                    2. 일치하지 않은 사용자 비밀번호입니다.
                    3. 휴면처리 된 계정입니다.
                    4. 탈퇴처리가 진행중인 계정입니다.
                    """,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<API<TokenResponse>> login(MemberLoginRequest request);

    @Operation(summary = "닉네임", description = "닉네임 중복 검사를 하는 기능")
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
    ResponseEntity<NoContent> validateNickname(String nickname);

    ResponseEntity<NoContent> validateEmail(String email);

}
