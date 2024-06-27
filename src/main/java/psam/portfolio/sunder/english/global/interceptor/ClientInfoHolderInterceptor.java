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
import psam.portfolio.sunder.english.infrastructure.clientinfo.ClientInfoHolder;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtClaim;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class ClientInfoHolderInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    private static final String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String loginId = null;
            String remoteIp = extractClientIp(request);

            String authorization = request.getHeader(AUTHORIZATION);
            if(StringUtils.hasText(authorization) && Pattern.matches("^Bearer .*", authorization)) {
                String token = authorization.replaceFirst("^Bearer ", "");
                if (jwtUtils.hasInvalidStatus(token).isEmpty()) {
                    loginId = jwtUtils.extractClaim(token, c -> c.get(JwtClaim.LOGIN_ID.toString(), String.class));
                }
            }
            ClientInfoHolder.syncClientInfo(loginId, remoteIp);
        } catch (Exception e) {
            // remoteIp 를 ThreadLocal 에 저장하는 과정에서 예외가 발생하면, ThreadLocal 에 저장된 remoteIp 를 해제하고 에러 응답을 보낸다.
            ClientInfoHolder.releaseClientInfo();
            sendError(response, ApiResponse.error(ApiStatus.INTERNAL_SERVER_ERROR, ClientInfoHolder.class, "Failed to sync client ip"));
            log.error("RemoteIpHolderInterceptor error", e);
            return false;
        }
        return true;
    }

    // afterCompletion : handler 에서 예외가 발생하더라도 무조건 실행
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ClientInfoHolder.releaseClientInfo();
    }

    private void sendError(HttpServletResponse response, ApiResponse<?> responseBody) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(responseBody));
        writer.flush();
        writer.close();
    }

    private String extractClientIp(HttpServletRequest request) {
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
