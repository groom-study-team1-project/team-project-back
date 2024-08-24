package deepdivers.community.global.security.jwt;

import deepdivers.community.domain.token.dto.TokenResponse;
import java.util.Map;

public interface TokenHelper {

    String issueAccessToken(Map<String, Object> data);
    String issueRefreshToken(Long memberId);
    void validationTokenWithThrow(String token);

}
