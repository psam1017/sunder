package psam.portfolio.sunder.english.infrastructure.username;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ClientUsernameHolder {

    private final ThreadLocal<String> local = new ThreadLocal<>();

    private static final String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    public String getClientUsername() {
        return local.get();
    }

    public void syncClientUsername(HttpServletRequest request) {
        local.set(extractClientIp(request));
    }

    public void syncClientUsername(String username) {
        local.set(username);
    }

    public void releaseClientUsername() {
        local.remove();
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
