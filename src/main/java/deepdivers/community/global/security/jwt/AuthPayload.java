package deepdivers.community.global.security.jwt;

public record AuthPayload (
        Long memberId,
        Long iat,
        Long exp
) {
}
