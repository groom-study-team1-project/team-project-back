package deepdivers.community.domain.token.service;

import deepdivers.community.domain.common.dto.response.API;
import deepdivers.community.domain.member.entity.Member;
import deepdivers.community.domain.token.dto.response.TokenResponse;
import deepdivers.community.domain.token.dto.code.TokenStatusCode;
import deepdivers.community.domain.token.exception.TokenExceptionCode;
import deepdivers.community.domain.common.exception.BadRequestException;
import deepdivers.community.global.security.AuthHelper;
import deepdivers.community.global.security.AuthPayload;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final AuthHelper authHelper;

    private static final String KEY_MEMBER_ID = "memberId";
    private static final String KEY_MEMBER_NICKNAME = "memberNickname";
    private static final String KEY_MEMBER_ROLE = "memberRole";
    private static final String KEY_MEMBER_IMAGE = "memberImageUrl";

    public TokenResponse generateToken(final Member member) {
        return getTokenResponse(
                createAccessTokenData(member),
                createRefreshTokenData(member)
        );
    }

    // Todo 블랙 리스트 관리
    public API<TokenResponse> reIssueAccessToken(final String bearerAccessToken, final String refreshToken) {
        final String accessToken = authHelper.resolveToken(bearerAccessToken);
        final AuthPayload authPayload = validateRefreshToken(accessToken, refreshToken);
        final TokenResponse response = generateToken(authPayload);

        return API.of(TokenStatusCode.RE_ISSUE_SUCCESS, response);
    }

    private TokenResponse generateToken(final AuthPayload memberInfo) {
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
        final AuthPayload accessTokenPayload = authHelper.parseToken(accessToken);
        final AuthPayload refreshTokenPayload = authHelper.parseToken(refreshToken);

        if (!accessTokenPayload.memberId().equals(refreshTokenPayload.memberId())) {
            throw new BadRequestException(TokenExceptionCode.MALFORMED_TOKEN);
        }

        return accessTokenPayload;
    }

    private Map<String, Object> createAccessTokenData(final Member member) {
        final Map<String, Object> data = new HashMap<>();
        data.put(KEY_MEMBER_ID, member.getId());
        data.put(KEY_MEMBER_NICKNAME, member.getNickname());
        data.put(KEY_MEMBER_ROLE, member.getRole());
        data.put(KEY_MEMBER_IMAGE, member.getImage().getImageUrl());
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
        data.put(KEY_MEMBER_IMAGE, payload.memberImageUrl());
        return data;
    }

    private Map<String, Object> createRefreshTokenData(final AuthPayload payload) {
        final Map<String, Object> data = new HashMap<>();
        data.put(KEY_MEMBER_ID, payload.memberId());
        return data;
    }

}
