package deepdivers.community.domain.global.security.jwt;

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
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final AuthHelper authHelper;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        log.info("Authorization Interceptor url : {}", request.getRequestURI());

        final String accessTokenReq = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String accessToken = authHelper.resolveToken(accessTokenReq);
        authHelper.validationTokenWithThrow(accessToken);
        final RequestAttributes context = Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        context.setAttribute("accessToken", accessToken, RequestAttributes.SCOPE_REQUEST);

        return true;
    }

}
