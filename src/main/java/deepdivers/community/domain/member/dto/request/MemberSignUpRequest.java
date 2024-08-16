package deepdivers.community.domain.member.dto.request;

import deepdivers.community.domain.member.dto.request.info.MemberAccountInfo;
import deepdivers.community.domain.member.dto.request.info.MemberRegisterInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 회원가입 하기")
public record MemberSignUpRequest(
        MemberAccountInfo memberAccountInfo,
        MemberRegisterInfo memberRegisterInfo
) {
}
