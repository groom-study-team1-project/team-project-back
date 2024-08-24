package deepdivers.community.domain.member.dto.response.result;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.model.vo.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 로그인 결과")
public record MemberLoginResult(
        @Schema(description = "사용자 식별자", example = "1")
        Long id,
        @Schema(description = "사용자 닉네임", example = "구름이")
        String nickname,
        @Schema(description = "사용자 구분", example = "STUDENT")
        MemberRole role
) {

    public static MemberLoginResult from(final Member member) {
        return new MemberLoginResult(member.getId(), member.getNickname(), member.getRole());
    }

}
