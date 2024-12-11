package deepdivers.community.domain.token.service;

import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.service.MemberService;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.domain.token.dto.TokenStatusType;
import deepdivers.community.domain.token.exception.TokenExceptionType;
import deepdivers.community.global.config.LocalStackTestConfig;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.security.jwt.AuthHelper;
import deepdivers.community.global.security.jwt.AuthPayload;
import deepdivers.community.global.utility.time.TimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(LocalStackTestConfig.class)
@Transactional
@DirtiesContext
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private TimeProvider timeProvider;

    @Value("${token.refresh-token.expiration-time}") long refreshTokenPlusHour;


    @Test
    @DisplayName("회원 정보로 토큰을 생성할 수 있어야 한다")
    void tokenGenerator_ShouldCreateValidTokens() {
        // Given, test.sql
        Member member = memberService.getMemberWithThrow(1L);

        // When
        TokenResponse tokenResponse = tokenService.tokenGenerator(member);

        // Then
        AuthPayload payload = authHelper.parseToken(tokenResponse.accessToken());

        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.accessToken()).isNotBlank();
        assertThat(tokenResponse.refreshToken()).isNotBlank();
        assertThat(payload.memberId()).isEqualTo(member.getId());
        assertThat(payload.memberNickname()).isEqualTo(member.getNickname());
        assertThat(payload.memberRole()).isEqualTo(member.getRole().toString());
    }

    @Test
    @DisplayName("refresh 토큰으로 토큰 재발급을 할 수 있어야 한다.")
    void reIssueAccessToken_ShouldGenerateNewValidTokens_WhenUsingRefreshToken() {
        // Given
        Member member = memberService.getMemberWithThrow(1L);

        TokenResponse initialTokens = tokenService.tokenGenerator(member);
        String bearerToken = "Bearer " + initialTokens.accessToken();
        Clock fixedClock = Clock.fixed(Instant.now().plusSeconds(60), ZoneId.systemDefault());
        timeProvider.setClock(fixedClock);

        // When
        API<TokenResponse> response = tokenService.reIssueAccessToken(bearerToken, initialTokens.refreshToken());

        // Then
        timeProvider.reset();
        AuthPayload initialPayload = authHelper.parseToken(initialTokens.accessToken());
        AuthPayload newPayload = authHelper.parseToken(response.result().accessToken());

        assertThat(response).isNotNull();
        assertThat(response.status().code()).isEqualTo(TokenStatusType.RE_ISSUE_SUCCESS.getCode());
        assertThat(response.result().accessToken()).isNotEqualTo(initialTokens.accessToken());
        assertThat(response.result().refreshToken()).isNotEqualTo(initialTokens.refreshToken());
        assertThat(newPayload.memberId()).isEqualTo(member.getId());
        assertThat(newPayload.exp()).isGreaterThan(initialPayload.exp());
    }

    @Test
    @DisplayName("refresh 토큰이 만료가 된 경우 예외를 발생해야 한다.")
    void reIssueAccessToken_ShouldThrowException_WhenRefreshTokenIsExpired() {
        // Given
        Member member = memberService.getMemberWithThrow(1L);

        Clock fixedClock = Clock.fixed(Instant.now().minusMillis(refreshTokenPlusHour), ZoneId.systemDefault());
        timeProvider.setClock(fixedClock);
        TokenResponse initialTokens = tokenService.tokenGenerator(member);
        timeProvider.reset();

        String bearerToken = "Bearer " + initialTokens.accessToken();

        // When, Then
        assertThatThrownBy(() -> tokenService.reIssueAccessToken(bearerToken, initialTokens.refreshToken()))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", TokenExceptionType.EXPIRED_TOKEN);
    }

}
