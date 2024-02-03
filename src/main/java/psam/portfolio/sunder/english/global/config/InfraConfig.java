package psam.portfolio.sunder.english.global.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import psam.portfolio.sunder.english.global.enumpattern.EnumPatternValidator;
import psam.portfolio.sunder.english.global.resolver.argument.UserIdArgumentResolver;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class InfraConfig {

    @Bean
    public JwtUtils jwtUtils(@Value("${sunder.security.token.secret-key}") String secretKey) {
        return new JwtUtils(secretKey);
    }

    @Bean
    public PasswordUtils passwordUtils(PasswordEncoder passwordEncoder) {
        return new PasswordUtils(passwordEncoder);
    }

    @Bean
    public MailUtils mailUtils(JavaMailSender javaMailSender) {
        return new MailUtils(javaMailSender);
    }
}
