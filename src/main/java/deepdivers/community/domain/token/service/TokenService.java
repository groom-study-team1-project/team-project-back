package deepdivers.community.domain.token.service;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.token.dto.TokenResponse;
import deepdivers.community.global.security.jwt.AuthHelper;
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

    public TokenResponse login(final Member member) {
        Map<String, Object> accessTokenData = createAccessTokenData(member);
        String accessToken = authHelper.issueAccessToken(accessTokenData);

        Map<String, Object> refreshTokenData = createRefreshTokenData(member);
        String refreshToken = authHelper.issueRefreshToken(refreshTokenData);

        return TokenResponse.of(accessToken, refreshToken);
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
