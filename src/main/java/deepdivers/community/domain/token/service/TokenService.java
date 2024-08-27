package deepdivers.community.domain.token.service;

import deepdivers.community.domain.common.StatusResponse;
import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.token.dto.ReissueResponse;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.domain.token.dto.TokenStatusType;
import deepdivers.community.domain.token.exception.TokenExceptionType;
import deepdivers.community.global.exception.model.BadRequestException;
import deepdivers.community.global.security.jwt.AuthHelper;
import deepdivers.community.global.security.jwt.AuthPayload;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final AuthHelper authHelper;

    private static final String KEY_MEMBER_ID = "memberId";
    private static final String KEY_MEMBER_NICKNAME = "memberNickname";
    private static final String KEY_MEMBER_ROLE = "memberRole";

    public TokenResponse tokenGenerator(final Member member) {
        return getTokenResponse(
                createAccessTokenData(member),
                createRefreshTokenData(member)
        );
    }

    public ReissueResponse reIssueAccessToken(final String bearerAccessToken, final String refreshToken) {
        final String accessToken = authHelper.resolveToken(bearerAccessToken);
        final AuthPayload authPayload = validateRefreshToken(accessToken, refreshToken);
        final TokenResponse response = tokenGenerator(authPayload);

        return new ReissueResponse(StatusResponse.from(TokenStatusType.RE_ISSUE_SUCCESS), response);
    }

    private TokenResponse tokenGenerator(final AuthPayload memberInfo) {
        return getTokenResponse(
                createAccessTokenData(memberInfo),
                createRefreshTokenData(memberInfo)
        );
    }

    private TokenResponse getTokenResponse(
            final Map<String, Object> accessTokenData,
            final Map<String, Object> refreshTokenData
    ) {
        final String accessToken = authHelper.issueAccessToken(accessTokenData);
        final String refreshToken = authHelper.issueRefreshToken(refreshTokenData);

        return TokenResponse.of(accessToken, refreshToken);
    }

    private AuthPayload validateRefreshToken(final String accessToken, final String refreshToken) {
        authHelper.validationTokenWithThrow(refreshToken);
        log.warn("Refresh token validation failed");
        final AuthPayload accessTokenPayload = authHelper.parseToken(accessToken);
        final AuthPayload refreshTokenPayload = authHelper.parseToken(refreshToken);

        if (!accessTokenPayload.memberId().equals(refreshTokenPayload.memberId())) {
            throw new BadRequestException(TokenExceptionType.MALFORMED_TOKEN);
        }

        return accessTokenPayload;
    }

    private Map<String, Object> createAccessTokenData(final Member member) {
        final Map<String, Object> data = new HashMap<>();
        data.put(KEY_MEMBER_ID, member.getId());
        data.put(KEY_MEMBER_NICKNAME, member.getNickname());
        data.put(KEY_MEMBER_ROLE, member.getRole());
        return data;
    }

    private Map<String, Object> createRefreshTokenData(final Member member) {
        final Map<String, Object> data = new HashMap<>();
        data.put(KEY_MEMBER_ID, member.getId());
        return data;
    }

    private Map<String, Object> createAccessTokenData(final AuthPayload payload) {
        final Map<String, Object> data = new HashMap<>();
        data.put(KEY_MEMBER_ID, payload.memberId());
        data.put(KEY_MEMBER_NICKNAME, payload.memberNickname());
        data.put(KEY_MEMBER_ROLE, payload.memberRole());
        return data;
    }

    private Map<String, Object> createRefreshTokenData(final AuthPayload payload) {
        final Map<String, Object> data = new HashMap<>();
        data.put(KEY_MEMBER_ID, payload.memberId());
        return data;
    }

}
