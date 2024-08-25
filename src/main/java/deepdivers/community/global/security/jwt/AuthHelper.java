package deepdivers.community.global.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface AuthHelper {

    String issueAccessToken(Map<String, Object> data);
    String issueRefreshToken(Long memberId);
    void validationTokenWithThrow(String token);
    String resolveToken(HttpServletRequest request);
}
