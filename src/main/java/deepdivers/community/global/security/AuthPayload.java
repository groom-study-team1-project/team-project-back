package deepdivers.community.global.security;

public record AuthPayload (
        Long memberId,
        String memberNickname,
        String memberRole,
        String memberImageUrl,
        Long iat,
        Long exp
) {
}
