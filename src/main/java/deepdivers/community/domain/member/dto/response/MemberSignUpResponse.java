package deepdivers.community.domain.member.dto.response;

import deepdivers.community.domain.common.ResultType;
import deepdivers.community.domain.member.model.Account;

public record MemberSignUpResponse(
        Integer code,
        String message,
        MemberSignUpResult result
) {

    public static MemberSignUpResponse of(final ResultType resultType, final Account account) {
        final MemberSignUpResult result = MemberSignUpResult.from(account);
        return new MemberSignUpResponse(resultType.getCode(), resultType.getMessage(), result);
    }

}
