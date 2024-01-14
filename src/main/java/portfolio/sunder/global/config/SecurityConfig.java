package portfolio.sunder.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import portfolio.sunder.domain.user.repository.UserQueryRepository;
import portfolio.sunder.global.security.JwtAuthenticationFilter;
import portfolio.sunder.global.security.UserDetailsServiceImpl;
import portfolio.sunder.infrastructure.jwt.JwtUtils;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    // TODO: 2024-01-14 OAUTH2
    // TODO: 2024-01-14 onAuthenticationFailure ?

    @Bean
    public JwtUtils jwtUtils(@Value("${sunder.security.token.secret-key}") String secretKey) {
        return new JwtUtils(secretKey);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(UserDetailsService userDetailsService, JwtUtils jwtUtils) {
        return new JwtAuthenticationFilter(userDetailsService, jwtUtils);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        return http
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers(
                                        PathRequest.toH2Console()
                                ).permitAll()
                                .requestMatchers(
                                        "/api/**",
                                        "/docs/**",
                                        "/css/**",
                                        "/js/**",
                                        "/image/**"
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserQueryRepository userQueryRepository) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService(userQueryRepository));
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService(UserQueryRepository userQueryRepository) {
        return new UserDetailsServiceImpl(userQueryRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
