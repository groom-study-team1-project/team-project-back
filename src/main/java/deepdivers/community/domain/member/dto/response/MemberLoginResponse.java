package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.common.StatusType;
import deepdivers.community.domain.member.dto.response.result.MemberLoginResult;
import deepdivers.community.domain.member.model.Member;

public record MemberLoginResponse(
        StatusResponse status,
        MemberLoginResult result
){

    public static MemberLoginResponse of(final StatusType statusType, final Member member) {
        final MemberLoginResult result = MemberLoginResult.from(member);
        final StatusResponse statusResponse = StatusResponse.from(statusType);
        return new MemberLoginResponse(statusResponse, result);
    }

}
