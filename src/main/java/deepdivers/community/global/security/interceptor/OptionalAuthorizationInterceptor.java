package deepdivers.community.global.security.interceptor;

import deepdivers.community.global.security.AuthHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@RequiredArgsConstructor
public class OptionalAuthorizationInterceptor implements HandlerInterceptor {

    private final AuthHelper authHelper;

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        log.info("Optional Authorization Interceptor ==> url : {}", request.getRequestURI());

        final String accessTokenReq = request.getHeader(HttpHeaders.AUTHORIZATION);
        final RequestAttributes context = Objects.requireNonNull(RequestContextHolder.getRequestAttributes());

        if (accessTokenReq != null) {
            final String accessToken = authHelper.resolveToken(accessTokenReq);
            authHelper.validationTokenWithThrow(accessToken);
            context.setAttribute("accessToken", accessToken, RequestAttributes.SCOPE_REQUEST);
        }

        return true;
    }

}
