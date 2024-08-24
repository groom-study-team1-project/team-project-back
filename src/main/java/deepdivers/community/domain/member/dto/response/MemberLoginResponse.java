package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.common.StatusType;
import deepdivers.community.domain.member.dto.response.result.MemberLoginResult;
import deepdivers.community.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 로그인 응답")
public record MemberLoginResponse(
        @Schema(description = "사용자 로그인 상태")
        StatusResponse status,
        @Schema(description = "사용자 로그인 결과")
        MemberLoginResult result
){

    public static MemberLoginResponse of(final StatusType statusType, final Member member) {
        final MemberLoginResult result = MemberLoginResult.from(member);
        final StatusResponse statusResponse = StatusResponse.from(statusType);
        return new MemberLoginResponse(statusResponse, result);
    }

}
