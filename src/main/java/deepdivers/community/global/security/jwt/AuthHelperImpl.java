package deepdivers.community.global.security.jwt;

import deepdivers.community.domain.token.exception.TokenExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AuthHelperImpl implements AuthHelper {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final String BLANK = " ";

    private final SecretKey secretKey;
    private final long accessTokenPlusHour;
    private final long refreshTokenPlusHour;

    public AuthHelperImpl(
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

    public String issueRefreshToken(final Long memberId) {
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + refreshTokenPlusHour);

        return Jwts.builder()
                .signWith(secretKey)
                .claim("memberId", memberId)
                .expiration(expiryDate)
                .issuedAt(now)
                .compact();
    }

    public void validationTokenWithThrow(final String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        } catch (final SignatureException e) {
            throw new BadRequestException(TokenExceptionType.UNSUPPORTED_TOKEN);
        } catch (final ExpiredJwtException e) {
            throw new BadRequestException(TokenExceptionType.EXPIRED_TOKEN);
        } catch (final UnsupportedJwtException e) {
            throw new BadRequestException(TokenExceptionType.SIGNATURE_TOKEN);
        } catch (final MalformedJwtException e) {
            throw new BadRequestException(TokenExceptionType.MALFORMED_TOKEN);
        } catch (final Exception e) {
            throw new BadRequestException(TokenExceptionType.UNKNOWN_TOKEN);
        }
    }

    public String resolveToken(final HttpServletRequest request) {
        final String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(token) && token.startsWith(BEARER_TOKEN_PREFIX)) {
            return token.split(BLANK)[1];
        }

        throw new BadRequestException(TokenExceptionType.NOT_FOUND_TOKEN);
    }

}
