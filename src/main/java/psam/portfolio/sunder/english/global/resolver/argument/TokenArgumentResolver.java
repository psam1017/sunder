package psam.portfolio.sunder.english.global.resolver.argument;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import psam.portfolio.sunder.english.infrastructure.jwt.IllegalTokenException;
import psam.portfolio.sunder.english.infrastructure.jwt.JwtUtils;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * token without "Bearer " prefix
 */
@Slf4j
@RequiredArgsConstructor
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtils jwtUtils;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean hasTokenAnnotation = parameter.hasParameterAnnotation(Token.class);
        boolean hasStringType = String.class.isAssignableFrom(parameter.getParameterType());
        return hasTokenAnnotation && hasStringType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authorization = request.getHeader(AUTHORIZATION);

        String token = authorization.replaceFirst("^Bearer ", "");
        jwtUtils.hasInvalidStatus(token).ifPresent(status -> {
            log.error("error occured at TokenArgumentResolver. authorization = {}, status = {}", authorization, status.name());
            throw new IllegalTokenException();
        });
        return token;
    }
}
