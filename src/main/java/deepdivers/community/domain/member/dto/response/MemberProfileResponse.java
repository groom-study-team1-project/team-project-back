package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.member.dto.response.result.ProfileImageUploadResult;
import deepdivers.community.domain.member.dto.response.result.type.MemberStatusType;
import deepdivers.community.domain.token.dto.TokenResponse;

public record MemberProfileResponse(
        StatusResponse status,
        ProfileImageUploadResult result
) {

    public static MemberProfileResponse of(
            final MemberStatusType memberStatusType,
            final String uploadUrl
    ) {
        final ProfileImageUploadResult result = new ProfileImageUploadResult(uploadUrl);
        return new MemberProfileResponse(StatusResponse.from(memberStatusType), result);
    }

}
