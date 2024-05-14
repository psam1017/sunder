package psam.portfolio.sunder.english.global.resolver.argument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import psam.portfolio.sunder.english.infrastructure.username.ClientUsernameHolder;

@Slf4j
@RequiredArgsConstructor
public class RemoteIpArgumentResolver implements HandlerMethodArgumentResolver {

    private final ClientUsernameHolder clientUsernameHolder;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean hasTokenAnnotation = parameter.hasParameterAnnotation(RemoteIp.class);
        boolean hasStringType = String.class.isAssignableFrom(parameter.getParameterType());
        return hasTokenAnnotation && hasStringType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return clientUsernameHolder.getClientUsername();
    }
}
