package psam.portfolio.sunder.english.global.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import psam.portfolio.sunder.english.global.enumpattern.EnumPatternValidator;
import psam.portfolio.sunder.english.global.resolver.argument.TokenArgumentResolver;
import psam.portfolio.sunder.english.global.resolver.argument.UserIdArgumentResolver;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

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
        resolvers.add(new TokenArgumentResolver(jwtUtils));
    }
}
