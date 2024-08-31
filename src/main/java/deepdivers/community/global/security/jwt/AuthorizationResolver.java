package deepdivers.community.global.security.jwt;

import deepdivers.community.domain.member.model.Member;
import deepdivers.community.domain.member.service.MemberService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;
    private final AuthHelper authHelper;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.withContainingClass(Member.class).hasParameterAnnotation(Auth.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) throws Exception {
        final RequestAttributes attrs = Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
        final String accessToken = (String) attrs.getAttribute("accessToken", RequestAttributes.SCOPE_REQUEST);
        final AuthPayload authPayload = authHelper.parseToken(accessToken);

        // Todo 캐싱 처리
        return memberService.getMemberWithThrow(authPayload.memberId());
    }

}
