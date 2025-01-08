package deepdivers.community.global.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.utility.time.TimeProvider;
import deepdivers.community.domain.token.exception.TokenExceptionType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AuthHelperImpl implements AuthHelper {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final String BLANK = " ";
    private static final String TOKEN_DELIMITER = "\\.";

    private final SecretKey secretKey;
    private final long accessTokenPlusHour;
    private final long refreshTokenPlusHour;
    private final ObjectMapper objectMapper;
    private final TimeProvider timeProvider;

    public AuthHelperImpl(
            @Value("${token.secret.key}") final String secretKey,
            @Value("${token.access-token.expiration-time}") final long accessTokenPlusHour,
            @Value("${token.refresh-token.expiration-time}") final long refreshTokenPlusHour,
            final ObjectMapper objectMapper,
            final TimeProvider timeProvider
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenPlusHour = accessTokenPlusHour;
        this.refreshTokenPlusHour = refreshTokenPlusHour;
        this.objectMapper = objectMapper;
        this.timeProvider = timeProvider;
    }

    public String issueAccessToken(final Map<String, Object> data) {
        final Date now = timeProvider.getCurrentDate();
        final Date expiryDate = new Date(now.getTime() + accessTokenPlusHour);

        return Jwts.builder()
                .signWith(secretKey)
                .claims(data)
                .expiration(expiryDate)
                .issuedAt(now)
                .compact();
    }

    public String issueRefreshToken(final Map<String, Object> data) {
        final Date now = timeProvider.getCurrentDate();
        final Date expiryDate = new Date(now.getTime() + refreshTokenPlusHour);

        return Jwts.builder()
                .signWith(secretKey)
                .claims(data)
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
        } catch (final ExpiredJwtException e) {
            throw new BadRequestException(TokenExceptionType.EXPIRED_TOKEN);
        } catch (final SignatureException e) {
            throw new BadRequestException(TokenExceptionType.SIGNATURE_TOKEN);
        }  catch (final UnsupportedJwtException e) {
            throw new BadRequestException(TokenExceptionType.UNSUPPORTED_TOKEN);
        } catch (final MalformedJwtException e) {
            throw new BadRequestException(TokenExceptionType.MALFORMED_TOKEN);
        } catch (final Exception e) {
            throw new BadRequestException(TokenExceptionType.UNKNOWN_TOKEN);
        }
    }

    public String resolveToken(final String bearerAccessToken) {
        if (StringUtils.hasText(bearerAccessToken) && bearerAccessToken.startsWith(BEARER_TOKEN_PREFIX)) {
            return bearerAccessToken.split(BLANK)[1];
        }

        throw new BadRequestException(TokenExceptionType.NOT_FOUND_TOKEN);
    }


    public AuthPayload parseToken(final String token) {
        final String[] chunks = token.split(TOKEN_DELIMITER);
        final String payload = new String(Decoders.BASE64.decode(chunks[1]));
        try {
            return objectMapper.readValue(payload, AuthPayload.class);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
