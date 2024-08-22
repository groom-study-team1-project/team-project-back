package deepdivers.community.domain.member.controller;

import deepdivers.community.domain.member.dto.request.MemberSignUpRequest;
import deepdivers.community.domain.member.dto.response.MemberSignUpResponse;
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
            responseCode = "200",
            description = """
                    1. 사용자 회원가입에 성공하였습니다.
                    """
    )
    @ApiResponse(
            responseCode = "400",
            description = """
                    1. 사용자 이메일 정보가 잘못되었습니다.
                    2. 사용자 비밀번호 정보가 잘못되었습니다.
                    3. 사용자 닉네임 정보가 잘못되었습니다.
                    4. 사용자 전화번호 정보가 잘못되었습니다. 
                    """,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<MemberSignUpResponse> signUp(MemberSignUpRequest request);

    ResponseEntity<String> login(String email, String password);

}
