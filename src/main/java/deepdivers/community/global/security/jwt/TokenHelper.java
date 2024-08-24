package deepdivers.community.global.security.jwt;

import deepdivers.community.domain.token.dto.TokenResponse;
import java.util.Map;

public interface TokenHelper {

    String issueAccessToken(Map<String, Object> data);
    void issueRefreshToken();
    void validationTokenWithThrow(String token);

}
