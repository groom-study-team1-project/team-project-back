package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.common.StatusType;
import deepdivers.community.domain.member.dto.response.result.MemberProfileResult;
import deepdivers.community.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 프로필 조회 응답")
public record MemberProfileResponse(
        @Schema(description = "사용자 프로필 조회 상태")
        StatusResponse status,
        @Schema(description = "사용자 프로필 조회 결과")
        MemberProfileResult result
) {

    public static MemberProfileResponse of(final StatusType status, final Member member) {
        return new MemberProfileResponse(StatusResponse.from(status), MemberProfileResult.from(member));
}

}
