package deepdivers.community.domain.global.config;

import deepdivers.community.domain.global.security.jwt.AuthorizationInterceptor;
import deepdivers.community.domain.global.security.jwt.AuthorizationResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthorizationInterceptor authorizationInterceptor;
    private final AuthorizationResolver authorizationResolver;

    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authorizationResolver);
    }

}