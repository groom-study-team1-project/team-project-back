package deepdivers.community.domain.post.aspect;

import deepdivers.community.domain.post.repository.jpa.PostRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ViewCountAspect {

    private static final String VIEW_COOKIE_PREFIX = "postView_";
    private static final int COOKIE_MAX_AGE = 24 * 60 * 60;

    private final PostRepository postRepository;

    @Around("@annotation(IncreaseViewCount)")
    public Object handleViewCount(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("====== view count aspect ======> ");
        try {
            final ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attr == null) {
                return joinPoint.proceed();
            }

            final HttpServletRequest request = attr.getRequest();
            final HttpServletResponse response = attr.getResponse();
            if (response == null) {
                return joinPoint.proceed();
            }

            final Long postId = (Long) joinPoint.getArgs()[0];
            final String cookieName = VIEW_COOKIE_PREFIX + postId;

            if (WebUtils.getCookie(request, cookieName) == null) {
                postRepository.incrementViewCount(postId);

                final Cookie cookie = new Cookie(cookieName, "true");
                cookie.setMaxAge(COOKIE_MAX_AGE);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        } catch (IllegalStateException | ClassCastException e) {
            log.warn("Failed to process view count: {}", e.getMessage());
        }

        return joinPoint.proceed();
    }

}