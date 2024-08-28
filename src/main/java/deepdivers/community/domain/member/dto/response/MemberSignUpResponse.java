package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.common.StatusType;
import deepdivers.community.domain.member.dto.response.result.MemberSignUpResult;
import deepdivers.community.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 회원가입 응답")
public record MemberSignUpResponse(
        @Schema(description = "사용자 회원가입 상태")
        StatusResponse status,
        @Schema(description = "사용자 회원가입 결과")
        MemberSignUpResult result
) {

    public static MemberSignUpResponse of(final StatusType statusType, final Member member) {
        final MemberSignUpResult result = MemberSignUpResult.from(member);
        final StatusResponse statusResponse = StatusResponse.from(statusType);
        return new MemberSignUpResponse(statusResponse, result);
    }

}
