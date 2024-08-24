package deepdivers.community.domain.token.dto;

import java.time.LocalDateTime;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {

    public static TokenResponse from(final String accessToken, final String refreshToken) {
        return new TokenResponse(accessToken, refreshToken);
    }

}