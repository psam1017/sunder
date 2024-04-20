package psam.portfolio.sunder.english.global.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import psam.portfolio.sunder.english.global.interceptor.AccessLogInterceptor;
import psam.portfolio.sunder.english.global.interceptor.RemoteIpHolderInterceptor;
import psam.portfolio.sunder.english.global.interceptor.JwtIpAuthenticationInterceptor;
import psam.portfolio.sunder.english.global.resolver.argument.RemoteIpArgumentResolver;
import psam.portfolio.sunder.english.global.validator.EnumPatternValidator;
import psam.portfolio.sunder.english.global.resolver.argument.TokenArgumentResolver;
import psam.portfolio.sunder.english.global.resolver.argument.UserIdArgumentResolver;
import psam.portfolio.sunder.english.infrastructure.ip.ClientIpHolder;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtUtils jwtUtils;
    private final ClientIpHolder clientIpHolder;
    private final Environment env;

    @Bean
    public EnumPatternValidator enumPatternValidator() {
        return new EnumPatternValidator();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true); // 필요한 경우 프론트엔드와 협의.
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserIdArgumentResolver(jwtUtils));
        resolvers.add(new TokenArgumentResolver(jwtUtils));
        resolvers.add(new RemoteIpArgumentResolver(clientIpHolder));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RemoteIpHolderInterceptor(clientIpHolder, objectMapper()))
                .addPathPatterns("/api/**")
                .order(1);
        registry.addInterceptor(new AccessLogInterceptor(clientIpHolder))
                .addPathPatterns("/api/**")
                .order(2);

        // "test" 프로필이 아닐 때만 Interceptor 를 등록
        if (!Arrays.asList(env.getActiveProfiles()).contains("test")) {
            registry.addInterceptor(new JwtIpAuthenticationInterceptor(jwtUtils, clientIpHolder, objectMapper()))
                    .addPathPatterns("/api/**")
                    .order(3);
        }
    }
}
