package deepdivers.community.global.security.jwt;

public record AuthPayload (
        Long memberId,
        String memberNickname,
        String memberRole,
        Long iat,
        Long exp
) {
}
