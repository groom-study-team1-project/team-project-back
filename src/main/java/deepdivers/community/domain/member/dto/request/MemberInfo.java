package deepdivers.community.domain.member.dto.request;

public record MemberInfo(
        String nickname,
        String imageUrl,
        String tel
) {
}
