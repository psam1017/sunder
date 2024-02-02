package psam.portfolio.sunder.english.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.web.user.model.entity.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @see <a href="https://docs.spring.io/spring-security/reference/servlet/authentication/events.html">Authentication Events</a>
 * also see <a href="https://www.baeldung.com/spring-security-custom-authentication-failure-handler">Spring Security Custom AuthenticationFailureHandler</a>
 */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // password not match
        if(exception instanceof BadCredentialsException) {
            sendError(response, ApiResponse.error(ApiStatus.FORBIDDEN, User.class, "BAD_CREDENTIALS", "The user's credentials are incorrect."));

        // isAccountNonLocked == false
        } else if(exception instanceof LockedException) {
            sendError(response, ApiResponse.error(ApiStatus.FORBIDDEN, User.class, "LOCKED", "The user's account is locked."));

        // isEnabled == false
        } else if(exception instanceof DisabledException) {
            sendError(response, ApiResponse.error(ApiStatus.FORBIDDEN, User.class, "DISABLED", "The user's account is disabled."));

        // isAccountNonExpired == false
        } else if(exception instanceof AccountExpiredException) {
            sendError(response, ApiResponse.error(ApiStatus.FORBIDDEN, User.class, "EXPIRED", "The user's account has expired."));

        // isCredentialsNonExpired == false
        } else if(exception instanceof CredentialsExpiredException) {
            sendError(response, ApiResponse.error(ApiStatus.FORBIDDEN, User.class, "CREDENTIALS_EXPIRED", "The user's credentials have expired."));

        // The user does not exist in the database, or there may be an illegal token request.
        } else if (exception instanceof UsernameNotFoundException) {
            log.error("[UsernameNotFoundException] {}", exception.getMessage());
            sendError(response, ApiResponse.error(ApiStatus.FORBIDDEN, User.class, "NOT_FOUND", "The user does not exist."));

        // There may be a structural problem with the AuthenticationService
        } else if (exception instanceof AuthenticationServiceException) {
            log.error("[AuthenticationServiceException] {}", exception.getMessage());
            sendError(response, ApiResponse.error(ApiStatus.FORBIDDEN, User.class, "SERVICE", "An error occurred in the authentication service."));

        // other exception
        } else {
            log.error("[AuthenticationException] {}", exception.getMessage());
            sendError(response, ApiResponse.error(ApiStatus.FORBIDDEN, User.class, "UNKNOWN", "An unknown error occurred."));
        }
    }

    private void sendError(HttpServletResponse response, ApiResponse<?> responseBody) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setStatus(HttpStatus.FORBIDDEN.value());

        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(responseBody));
        writer.flush();
        writer.close();
    }
}
