package deepdivers.community.global.security.jwt;

import java.util.Map;

public interface AuthHelper {

    String issueAccessToken(Map<String, Object> data);
    String issueRefreshToken(Map<String, Object> data);
    void validationTokenWithThrow(String token);
    String resolveToken(String token);
    AuthPayload parseToken(String token);

}
