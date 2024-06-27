package psam.portfolio.sunder.english.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import psam.portfolio.sunder.english.infrastructure.clientinfo.ClientInfoHolder;

@Slf4j
@RequiredArgsConstructor
public class AccessLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            if (handler instanceof HandlerMethod) {
                String access = ClientInfoHolder.getUsername();
                if (!StringUtils.hasText(access)) {
                    access = ClientInfoHolder.getRemoteIp();
                }
                log.info("{} -> {} {}", access, request.getMethod(), request.getRequestURI());
            }
            return true;
        } catch (Exception e) {
            log.error("AccessLogInterceptor error", e);
            return true;
        }
    }
}
