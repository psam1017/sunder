package portfolio.sunder.global.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import portfolio.sunder.global.enumpattern.EnumPatternValidator;
import portfolio.sunder.global.resolver.argument.UserIdArgumentResolver;
import portfolio.sunder.infrastructure.jwt.JwtUtils;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final JwtUtils jwtUtils;

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
    }
}
