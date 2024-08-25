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

    public TokenResponse login(final Member member) {
        final Map<String, Object> data = new HashMap<>();
        data.put("memberId", member.getId());
        data.put("memberNickname", member.getNickname());
        data.put("memberRole", member.getRole());

        final String accessToken = authHelper.issueAccessToken(data);
        final String refreshToken = authHelper.issueRefreshToken(member.getId());

        return TokenResponse.of(accessToken, refreshToken);
    }

}
