package deepdivers.community.global.security.jwt;

import deepdivers.community.domain.token.dto.TokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenHelperImpl implements TokenHelper {

    private final Key secretKey;
    private final long accessTokenPlusHour;
    private final long refreshTokenPlusHour;

    public TokenHelperImpl(
            @Value("${token.secret.key}") final String secretKey,
            @Value("${token.access-token.expiration-time}") final int accessTokenPlusHour,
            @Value("${token.refresh-token.expiration-time}") final int refreshTokenPlusHour
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenPlusHour = accessTokenPlusHour;
        this.refreshTokenPlusHour = refreshTokenPlusHour;
    }

    public String issueAccessToken(final Map<String, Object> data) {
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + accessTokenPlusHour);

        return Jwts.builder()
                .signWith(secretKey)
                .claims(data)
                .expiration(expiryDate)
                .issuedAt(now)
                .compact();
    }

    public void issueRefreshToken() {

    }

    public void validationTokenWithThrow(String token) {

    }
}
