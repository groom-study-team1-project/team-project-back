package deepdivers.community.domain.member.controller.docs;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.common.NoContent;
import deepdivers.community.domain.member.dto.request.MemberProfileRequest;
import deepdivers.community.domain.member.dto.request.UpdatePasswordRequest;
import deepdivers.community.domain.member.dto.response.AllMyPostsResponse;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import deepdivers.community.global.security.jwt.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "2. 회원 - 토큰", description = "토큰 정보가 필요한 회원 관련 API")
public interface MemberApiControllerDocs {

    @Operation(summary = "프로필 조회", description = "프로필을 조회하는 기능")
    @ApiResponse(
            responseCode = "1002, 1003",
            description = """
                    1. 본인 프로필 조회에 성공하였습니다.
                    2. 다른 사용자의 프로필 조회에 성공하였습니다.
                    """
    )
    @ApiResponse(
            responseCode = "2009\n9000~9005",
            description = """
                    1. 사용자 정보를 찾을 수 없습니다.
                    2. 토큰 관련 예외입니다.
                    """,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<API<MemberProfileResponse>> me(Member member, Long profileOwnerId);

    @Operation(summary = "이미지 업로드", description = "프로필 이미지를 업로드하는 기능")
    @ApiResponse(
            responseCode = "1004",
            description = """
                    1. 사용자 프로필 이미지 업로드에 성공하였습니다.
                    """
    )
    @ApiResponse(
            responseCode = "2009\n9000~9005",
            description = """
                    1. 사용자 정보를 찾을 수 없습니다.
                    2. 토큰 관련 예외입니다.
                    """,
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<API<ImageUploadResponse>> profileImageUpload(Member member, MultipartFile imageFile);

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
    ResponseEntity<API<MemberProfileResponse>> updateProfile(Member member, MemberProfileRequest request);

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

    @Operation(summary = "내가 작성한 게시글", description = "내가 작성한 게시글을 조회하는 기능")
    @ApiResponse(
        responseCode = "1009\n9000~9005",
        description = """
                    1. 내가 쓴 게시글 조회에 성공하였습니다.
                    """
    )
    @ApiResponse(
        responseCode = "9000~9005",
        description = """
                    1. 토큰 관련 예외입니다.
                    """,
        content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
    )
    ResponseEntity<API<List<AllMyPostsResponse>>> allWrittenPosts(Member member, Long categoryId, Long lastPostId);

}
