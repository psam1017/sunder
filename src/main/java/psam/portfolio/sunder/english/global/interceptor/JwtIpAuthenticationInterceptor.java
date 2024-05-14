package psam.portfolio.sunder.english.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;
import psam.portfolio.sunder.english.infrastructure.username.ClientUsernameHolder;
import psam.portfolio.sunder.english.infrastructure.jwt.IllegalTokenException;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtClaim;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtStatus;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static psam.portfolio.sunder.english.infrastructure.jwt.JwtStatus.ILLEGAL_IP_ACCESS;

@Slf4j
@RequiredArgsConstructor
public class JwtIpAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final ClientUsernameHolder clientUsernameHolder;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader(AUTHORIZATION);

        if(StringUtils.hasText(authorization) && Pattern.matches("^Bearer .*", authorization)) {
            String token = authorization.replaceAll("^Bearer( )*", "");
            jwtUtils.hasInvalidStatus(token).ifPresent(status -> {
                log.error("error occurred at JwtIpAuthenticationInterceptor. authorization = {}, status = {}", authorization, status.name());
                throw new IllegalTokenException();
            });
            String tokenIp = jwtUtils.extractClaim(token, c -> c.get(JwtClaim.REMOTE_IP.toString(), String.class));
            String remoteIp = clientUsernameHolder.getClientUsername();

            if (!Objects.equals(tokenIp, remoteIp)) {
                log.error("Illegal Ip Access. subject : {}, tokenIp: {}, remoteIp: {}", jwtUtils.extractSubject(token), tokenIp, remoteIp);
                sendError(response, ApiResponse.error(ApiStatus.FORBIDDEN, JwtStatus.class, ILLEGAL_IP_ACCESS.toString(), "Illegal Ip Access"));
                return false;
            }
        }
        return true;
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
