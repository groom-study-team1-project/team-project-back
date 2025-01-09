package deepdivers.community.domain.member.controller.docs;

import deepdivers.community.domain.common.dto.response.NoContent;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.common.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "02. 회원 - 토큰", description = "토큰 정보가 필요한 회원 관련 API")
public interface MemberApiControllerDocs {

    @Operation(summary = "프로필 수정", description = "프로필을 수정하는 기능")
    @ApiResponse(
        responseCode = "1007\n9000~9005",
        description = """
                    1. 프로필 수정이 성공하였습니다.
                    2. 토큰 관련 예외입니다.
                    """
    )
    @ApiResponse(
        responseCode = "2001, 2002, 2004, 2009\n9000~9005",
        description = """
                    1. 사용자 닉네임은 2글자부터 최대 20자입니다.
                    2. 사용자 닉네임은 영어 대소문자와 한글 및 숫자의 조합으로 작성해주세요.
                    3. 이미 가입된 사용자 닉네임입니다.
                    4. 사용자 정보를 찾을 수 없습니다.
                    5. 토큰 관련 예외입니다.
                    """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> updateProfile(Member member, MemberProfileRequest request);

    @Operation(summary = "비밀번호 수정", description = "비밀번호를 수정하는 기능")
    @ApiResponse(
        responseCode = "1008\n9000~9005",
        description = """
                    1. 비밀번호 변경이 성공하였습니다.
                    2. 토큰 관련 예외입니다.
                    """
    )
    @ApiResponse(
        responseCode = "2000, 2011, 2012\n9000~9005",
        description = """
                    1. 사용자 비밀번호는 8글자부터 16글자로 영어 소문자, 특수문자, 숫자를 조합해주세요.
                    2. 비밀번호가 틀렸습니다.
                    3. 동일한 비밀번호로 변경할 수 없습니다.
                    4. 토큰 관련 예외입니다.
                    """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<NoContent> updatePassword(Member member, UpdatePasswordRequest request);

}
