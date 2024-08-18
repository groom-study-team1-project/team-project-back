package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.ResultType;
import deepdivers.community.domain.member.dto.response.result.MemberSignUpResult;
import deepdivers.community.domain.member.model.Account;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 회원가입 응답")
public record MemberSignUpResponse(
        @Schema(description = "응답 코드", example = "1000")
        Integer code,
        @Schema(description = "응답 코드", example = "사용자 회원가입에 성공하였습니다.")
        String message,
        MemberSignUpResult result
) {

    public static MemberSignUpResponse of(final ResultType resultType, final Account account) {
        final MemberSignUpResult result = MemberSignUpResult.from(account);
        return new MemberSignUpResponse(resultType.getCode(), resultType.getMessage(), result);
    }

}
