package deepdivers.community.domain.token.service;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.token.dto.TokenResponse;
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
        Map<String, Object> accessTokenData = createAccessTokenData(member);
        String accessToken = authHelper.issueAccessToken(accessTokenData);

        Map<String, Object> refreshTokenData = createRefreshTokenData(member);
        String refreshToken = authHelper.issueRefreshToken(refreshTokenData);

        return TokenResponse.of(accessToken, refreshToken);
    }

    public TokenResponse reIssueAccessToken(final Member member, final String refreshToken) {
        authHelper.validationTokenWithThrow(refreshToken);
        final AuthPayload authPayload = authHelper.parseToken(refreshToken);
        if (!authPayload.memberId().equals(member.getId())) {
            throw new BadRequestException(TokenExceptionType.MALFORMED_TOKEN);
        }

        return tokenGenerator(member);
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

}
