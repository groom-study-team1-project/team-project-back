package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.member.dto.response.result.ProfileImageUploadResult;
import deepdivers.community.domain.member.dto.response.result.type.MemberStatusType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로필 이미지 업로드 응답")
public record MemberProfileImageResponse(
        @Schema(description = "사용자 프로필 조회 상태")
        StatusResponse status,
        @Schema(description = "응답 결과")
        ProfileImageUploadResult result
) {

    public static MemberProfileImageResponse of(
            final MemberStatusType memberStatusType,
            final String uploadUrl
    ) {
        final ProfileImageUploadResult result = new ProfileImageUploadResult(uploadUrl);
        return new MemberProfileImageResponse(StatusResponse.from(memberStatusType), result);
    }

}
