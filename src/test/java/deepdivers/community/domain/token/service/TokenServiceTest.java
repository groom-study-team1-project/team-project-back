package deepdivers.community.domain.token.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import deepdivers.community.domain.ServiceTest;
import deepdivers.community.domain.common.API;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.domain.token.exception.TokenExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.security.AuthHelper;
import deepdivers.community.global.security.AuthPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TokenServiceTest extends ServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthHelper authHelper;

    @Test
    @DisplayName("회원 정보로 토큰을 생성할 수 있어야 한다")
    void generateToken_ShouldCreateValidTokens() {
        // Given, test.sql
        Member member = getMember(1L);

        // When
        TokenResponse tokenResponse = tokenService.generateToken(member);

        // Then
        AuthPayload payload = authHelper.parseToken(tokenResponse.accessToken());
        assertThat(payload.memberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("refresh 토큰으로 토큰 재발급을 할 수 있어야 한다.")
    void reIssueAccessToken_ShouldGenerateNewValidTokens_WhenUsingRefreshToken() {
        // Given
        TokenResponse initialTokens = tokenService.generateToken(getMember(1L));
        String bearerToken = "Bearer " + initialTokens.accessToken();

        // When
        API<TokenResponse> response = tokenService.reIssueAccessToken(bearerToken, initialTokens.refreshToken());

        // Then
        AuthPayload result = authHelper.parseToken(response.result().accessToken());
        assertThat(result.memberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("access Token과 refresh Token의 memberId가 다르다면 예외가 발생한다.")
    void givenHavingAnotherMemberIdAccessTokenAndRefreshTokenWhenReIssueAccessTokenThenThrowException() {
        // Given
        TokenResponse member1Token = tokenService.generateToken(getMember(1L));
        TokenResponse member2Token = tokenService.generateToken(getMember(2L));
        String bearerToken = "Bearer " + member1Token.accessToken();

        // When, Then
        assertThatThrownBy(() -> tokenService.reIssueAccessToken(bearerToken, member2Token.refreshToken()))
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("exceptionType", TokenExceptionType.MALFORMED_TOKEN);
    }

}
