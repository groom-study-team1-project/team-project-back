package deepdivers.community.global.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import deepdivers.community.domain.token.exception.TokenExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.utility.time.TimeProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthHelperTest {

    private AuthHelper authHelper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TimeProvider timeProvider;

    private static final String MEMBER_ID = "memberId";
    private final String secretKey = "thisIsATestSecretKeyForJwtTokenGenerationAndValidation";
    private final long accessTokenExpirationTime = 3600000;
    private final long refreshTokenExpirationTime = 86400000;

    @BeforeEach
    void setUp() {
        authHelper = new AuthHelperImpl(secretKey, accessTokenExpirationTime, refreshTokenExpirationTime, objectMapper, timeProvider);
    }

    @Test
    @DisplayName("액세스 토큰 발급 시 유효한 토큰을 반환해야 한다")
    void issueAccessToken_ShouldReturnValidToken() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(MEMBER_ID, 1L);
        when(timeProvider.getCurrentDate()).thenReturn(new Date());

        // When
        String token = authHelper.issueAccessToken(claims);

        // Then
        assertThat(token).isNotEmpty();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Long userId = Long.parseLong(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get(MEMBER_ID).toString());
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("리프레시 토큰 발급 시 유효한 토큰을 반환해야 한다")
    void issueRefreshToken_ShouldReturnValidToken() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(MEMBER_ID, 1L);
        when(timeProvider.getCurrentDate()).thenReturn(new Date());

        // When
        String token = authHelper.issueRefreshToken(claims);

        // Then
        assertThat(token).isNotEmpty();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Long userId = Long.parseLong(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get(MEMBER_ID).toString());
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("유효한 토큰 검증 시 예외가 발생하지 않아야 한다")
    void validationTokenWithThrow_ShouldNotThrowForValidToken() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(MEMBER_ID, 1L);
        when(timeProvider.getCurrentDate()).thenReturn(new Date());
        String token = authHelper.issueAccessToken(claims);

        // When & Then
        assertThatCode(() -> authHelper.validationTokenWithThrow(token))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("유효하지 않은 토큰 검증 시 예외가 발생해야 한다")
    void validationTokenWithThrow_ShouldThrowForInvalidToken() {
        // Given
        String invalidToken = "invalidToken";

        // When & Then
        assertThatThrownBy(() -> authHelper.validationTokenWithThrow(invalidToken))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Bearer 접두사가 있는 토큰에서 실제 토큰 값을 추출해야 한다")
    void resolveToken_ShouldReturnTokenWithoutPrefix() {
        // Given
        String bearerToken = "Bearer tokenValue";

        // When
        String resolvedToken = authHelper.resolveToken(bearerToken);

        // Then
        assertThat(resolvedToken).isEqualTo("tokenValue");
    }

    @Test
    @DisplayName("토큰 파싱 시 올바른 AuthPayload를 반환해야 한다")
    void parseToken_ShouldReturnAuthPayload() throws JsonProcessingException {
        // Given
        String token = "header.eyJtZW1iZXJJZCI6MX0.signature";
        AuthPayload expectedPayload = new AuthPayload(1L, "testUser", "USER", accessTokenExpirationTime, refreshTokenExpirationTime);
        when(objectMapper.readValue("{\"memberId\":1}", AuthPayload.class)).thenReturn(expectedPayload);

        // When
        AuthPayload result = authHelper.parseToken(token);

        // Then
        assertThat(result).isEqualTo(expectedPayload);
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 EXPIRED_TOKEN 예외가 발생해야 한다")
    void validationTokenWithThrow_ShouldThrowForExpiredToken() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put(MEMBER_ID, 1L);
        String token = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .compact();

        // When & Then
        assertThatThrownBy(() -> authHelper.validationTokenWithThrow(token))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", TokenExceptionType.EXPIRED_TOKEN);
    }

    @Test
    @DisplayName("지원되지 않는 토큰 검증 시 UNSUPPORTED_TOKEN 예외가 발생해야 한다")
    void validationTokenWithThrow_ShouldThrowForUnsupportedToken() {
        // Given
        String unsupportedToken = Jwts.builder()
                .setPayload("payload") // 지원되지 않는 방식으로 페이로드 설정
                .compact();

        // When & Then
        assertThatThrownBy(() -> authHelper.validationTokenWithThrow(unsupportedToken))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", TokenExceptionType.UNSUPPORTED_TOKEN);
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 검증 시 MALFORMED_TOKEN 예외가 발생해야 한다")
    void validationTokenWithThrow_ShouldThrowForMalformedToken() {
        // Given
        String malformedToken = "malformed.token.here";

        // When & Then
        assertThatThrownBy(() -> authHelper.validationTokenWithThrow(malformedToken))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", TokenExceptionType.MALFORMED_TOKEN);
    }

    @Test
    @DisplayName("서명이 유효하지 않은 토큰 검증 시 SIGNATURE_TOKEN 예외가 발생해야 한다")
    void validationTokenWithThrow_ShouldThrowForSignatureToken() {
        // Given
        String invalidSignatureToken = Jwts.builder()
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256)) // 다른 키로 서명
                .setSubject("test")
                .compact();

        // When & Then
        assertThatThrownBy(() -> authHelper.validationTokenWithThrow(invalidSignatureToken))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", TokenExceptionType.SIGNATURE_TOKEN);
    }

    @Test
    @DisplayName("알 수 없는 토큰 검증 시 UNKNOWN_TOKEN 예외가 발생해야 한다")
    void validationTokenWithThrow_ShouldThrowForUnknownToken() {
        // Given
        String unknownToken = null;

        // When & Then
        assertThatThrownBy(() -> authHelper.validationTokenWithThrow(unknownToken))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", TokenExceptionType.UNKNOWN_TOKEN);
    }

}