package deepdivers.community.domain.token.dto;

import java.time.LocalDateTime;

public record TokenResponse(
        String accessToken
) {

    public static TokenResponse from(final String accessToken) {
        return new TokenResponse(accessToken);
    }

}