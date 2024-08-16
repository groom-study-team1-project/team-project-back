package deepdivers.community.domain.member.dto.request;

public record MemberSignUpRequest(
        MemberAccount memberAccount,
        MemberInfo memberInfo
) {
}
