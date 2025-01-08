package deepdivers.community.global.config;

import deepdivers.community.global.security.interceptor.AuthorizationInterceptor;
import deepdivers.community.global.security.interceptor.OptionalAuthorizationInterceptor;
import deepdivers.community.global.security.resolver.AuthorizationResolver;
import deepdivers.community.global.security.resolver.OptionalAuthorizationResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthorizationInterceptor authorizationInterceptor;
    private final AuthorizationResolver authorizationResolver;
    private final OptionalAuthorizationInterceptor optionalAuthorizationInterceptor;
    private final OptionalAuthorizationResolver optionalAuthorizationResolver;

    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(optionalAuthorizationInterceptor)
            .addPathPatterns("/open/posts/**")
            .addPathPatterns("/open/members/me/**");
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authorizationResolver);
        resolvers.add(optionalAuthorizationResolver);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(false)
            .maxAge(3600);
    }

}
