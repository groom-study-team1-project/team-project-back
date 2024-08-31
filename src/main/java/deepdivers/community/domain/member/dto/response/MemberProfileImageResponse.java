package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.member.dto.response.result.ProfileImageUploadResult;
import deepdivers.community.domain.member.dto.response.result.type.MemberStatusType;

public record MemberProfileImageResponse(
        StatusResponse status,
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
