package deepdivers.community.domain.member.dto.response.result;

import deepdivers.community.domain.member.model.Member;

public record MemberLoginResult(
        Long memberId
) {

    public static MemberLoginResult from(Member member) {
        return new MemberLoginResult(member.getId());
    }

}
