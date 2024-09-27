package deepdivers.community.domain.global.security.jwt;

public record AuthPayload (
        Long memberId,
        String memberNickname,
        String memberRole,
        String memberImageUrl,
        Long iat,
        Long exp
) {
}
