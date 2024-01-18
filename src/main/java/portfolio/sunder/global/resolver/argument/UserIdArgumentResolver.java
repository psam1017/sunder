package portfolio.sunder.global.resolver.argument;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import portfolio.sunder.infrastructure.jwt.IllegalTokenException;
import portfolio.sunder.infrastructure.jwt.JwtUtils;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtils jwtUtils;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean hasTokenAnnotation = parameter.hasParameterAnnotation(UserId.class);
        boolean hasLongType = Long.class.isAssignableFrom(parameter.getParameterType());
        return hasTokenAnnotation && hasLongType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String authorization = request.getHeader(AUTHORIZATION);

        String token = authorization.replaceAll("^Bearer( )*", "");
        jwtUtils.hasInvalidStatus(token).ifPresent(js -> {
            log.error("error occured at UserIdArgumentResolver. authorization = {}, status = {}", authorization, js.name());
            throw new IllegalTokenException();
        });
        return jwtUtils.extractSubject(token);
    }
}
