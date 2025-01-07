package deepdivers.community.global.security.resolver;

import deepdivers.community.global.security.Auth;
import deepdivers.community.global.security.AuthHelper;
import deepdivers.community.global.security.AuthPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class OptionalAuthorizationResolver implements HandlerMethodArgumentResolver {

    private final AuthHelper authHelper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        final String requestURI = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes())
            .getRequest()
            .getRequestURI();

        return (requestURI.startsWith("/open/") &&
                parameter.withContainingClass(Long.class).hasParameterAnnotation(Auth.class));
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        final RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return 0L;
        }

        final String accessToken = (String) attrs.getAttribute("accessToken", RequestAttributes.SCOPE_REQUEST);
        if (accessToken == null) {
            return 0L;
        }

        try {
            final AuthPayload authPayload = authHelper.parseToken(accessToken);
            return authPayload.memberId();
        } catch (final Exception e) {
            return 0L;
        }
    }

}
