package deepdivers.community.domain.member.controller.api;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.dto.response.ImageUploadResponse;
import deepdivers.community.domain.member.dto.response.MemberProfileResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.global.exception.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자", description = "사용자 관련 API")
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

}
