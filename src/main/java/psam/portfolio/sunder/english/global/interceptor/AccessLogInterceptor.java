package psam.portfolio.sunder.english.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import psam.portfolio.sunder.english.infrastructure.ip.ClientIpHolder;

@Slf4j
@RequiredArgsConstructor
public class AccessLogInterceptor implements HandlerInterceptor {

    private final ClientIpHolder clientIpHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            if (handler instanceof HandlerMethod) {
                log.info("Remote Ip : {}", clientIpHolder.getClientIp());
                log.info("Access Log : {} {}", request.getMethod(), request.getRequestURI());
            }
            return true;
        } catch (Exception e) {
            log.error("TokenIpCheckInterceptor error", e);
            return true;
        }
    }
}
