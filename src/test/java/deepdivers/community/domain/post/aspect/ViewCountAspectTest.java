package deepdivers.community.domain.post.aspect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import deepdivers.community.domain.post.repository.jpa.PostRepository;
import jakarta.servlet.http.Cookie;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(value = MockitoExtension.class)
class ViewCountAspectTest {

    @InjectMocks private ViewCountAspect viewCountAspect;
    @Mock private PostRepository postRepository;
    @Mock private ProceedingJoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        ServletRequestAttributes attrs = new ServletRequestAttributes(mockRequest, mockResponse);
        RequestContextHolder.setRequestAttributes(attrs);
    }

    @Test
    void Aspect에서_조회수_증가_로직을_실행한다() throws Throwable {
        // given
        Long postId = 1L;
        when(joinPoint.getArgs()).thenReturn(new Object[]{postId});

        // when
        viewCountAspect.handleViewCount(joinPoint);

        // then
        verify(postRepository).incrementViewCount(postId);
    }

    @Test
    void requestAttributes가_없을_경우_조회수_증가_로직을_실행하지_않는다() throws Throwable {
        // given
        RequestContextHolder.resetRequestAttributes();
        when(joinPoint.proceed()).thenReturn("result");

        // when
        Object result = viewCountAspect.handleViewCount(joinPoint);

        // then
        assertEquals("result", result);
        verifyNoInteractions(postRepository);
    }

    @Test
    void response_정보가_없을_경우_조회수_증가_로직이_실행되지_않는다() throws Throwable {
        // given
        ServletRequestAttributes attrs = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(attrs);
        when(joinPoint.proceed()).thenReturn("result");

        // when
        Object result = viewCountAspect.handleViewCount(joinPoint);

        // then
        assertEquals("result", result);
        verifyNoInteractions(postRepository);
    }

    @Test
    void 이미_조회한_쿠키_정보가_있을_경우_조회수_증가_로직이_실행되지_않는다() throws Throwable {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("postView_1", "true"));
        ServletRequestAttributes attrs = new ServletRequestAttributes(request, new MockHttpServletResponse());
        RequestContextHolder.setRequestAttributes(attrs);

        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        when(joinPoint.proceed()).thenReturn("result");

        // when
        viewCountAspect.handleViewCount(joinPoint);

        // then
        verifyNoInteractions(postRepository);
    }

}