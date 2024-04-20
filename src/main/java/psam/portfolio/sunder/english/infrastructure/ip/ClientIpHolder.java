package psam.portfolio.sunder.english.infrastructure.ip;

import jakarta.servlet.http.HttpServletRequest;

public class ClientIpHolder {

    private final ThreadLocal<String> local = new ThreadLocal<>();

    private static final String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    public String getClientIp() {
        return local.get();
    }

    public void syncClientIp(HttpServletRequest request) {
        local.set(extractClientIp(request));
    }

    public void releaseClientIp() {
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
