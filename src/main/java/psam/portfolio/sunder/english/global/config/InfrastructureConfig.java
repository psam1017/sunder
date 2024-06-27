package psam.portfolio.sunder.english.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import psam.portfolio.sunder.english.infrastructure.excel.ExcelUtils;
import psam.portfolio.sunder.english.infrastructure.clientinfo.ClientInfoHolder;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;

@RequiredArgsConstructor
@Configuration
public class InfrastructureConfig {

    @Bean
    public JwtUtils jwtUtils(@Value("${sunder.security.token.secret-key}") String secretKey) {
        return new JwtUtils(secretKey);
    }

    @Bean
    public PasswordUtils passwordUtils(PasswordEncoder passwordEncoder) {
        return new PasswordUtils(passwordEncoder);
    }

    @Bean
    public MailUtils mailUtils(JavaMailSender javaMailSender, @Value("${spring.mail.username}") String fromEmail) {
        return new MailUtils(javaMailSender, fromEmail);
    }

    @Bean
    public ExcelUtils excelUtils() {
        return new ExcelUtils();
    }
}
