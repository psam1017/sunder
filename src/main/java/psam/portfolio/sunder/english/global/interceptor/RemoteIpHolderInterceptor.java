package psam.portfolio.sunder.english.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;
import psam.portfolio.sunder.english.infrastructure.ip.ClientIpHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class RemoteIpHolderInterceptor implements HandlerInterceptor {

    private final ClientIpHolder clientIpHolder;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            clientIpHolder.syncClientIp(request);
        } catch (Exception e) {

            // remoteIp 를 ThreadLocal 에 저장하는 과정에서 예외가 발생하면, ThreadLocal 에 저장된 remoteIp 를 해제하고 에러 응답을 보낸다.
            clientIpHolder.releaseClientIp();
            sendError(response, ApiResponse.error(ApiStatus.INTERNAL_SERVER_ERROR, ClientIpHolder.class, "Failed to sync client ip"));
            log.error("RemoteIpHolderInterceptor error", e);
            return false;
        }
        return true;
    }

    // afterCompletion : handler 에서 예외가 발생하더라도 무조건 실행
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        clientIpHolder.releaseClientIp();
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
}
