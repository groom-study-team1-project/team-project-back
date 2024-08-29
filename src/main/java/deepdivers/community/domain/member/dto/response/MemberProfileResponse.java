package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.common.StatusType;
import deepdivers.community.domain.member.dto.response.result.MemberProfileResult;
import deepdivers.community.domain.member.model.Member;

public record MemberProfileResponse(
        StatusResponse status,
        MemberProfileResult result
) {

    public static MemberProfileResponse of(final StatusType status, final Member member) {
        return new MemberProfileResponse(StatusResponse.from(status), MemberProfileResult.from(member));
    }

}
