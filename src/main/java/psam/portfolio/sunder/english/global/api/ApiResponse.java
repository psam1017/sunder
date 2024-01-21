package psam.portfolio.sunder.english.global.api;

import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.validation.BindException;

import java.util.List;

import static psam.portfolio.sunder.english.global.api.ApiStatus.BAD_REQUEST;
import static psam.portfolio.sunder.english.global.api.ApiStatus.OK;

@Getter
public class ApiResponse<T> {

    private final String code;
    private final String message;
    private final List<String> reasons;
    private final T data;

    private ApiResponse(ApiStatus apiStatus, List<String> reasons, T data){
        this.code = apiStatus.code();
        this.message = apiStatus.message();
        this.reasons = reasons;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(ApiStatus status, T data) {
        return new ApiResponse<>(status, List.of(status.message()), data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(OK, List.of(OK.message()), data);
    }

    public static <T> ApiResponse<T> error(ApiStatus status, Class<?> clazz, String concrete, T data) {
        return new ApiResponse<>(status, List.of(status.message() + "." + clazz.getSimpleName() + "." + concrete), data);
    }

    public static <T> ApiResponse<T> error(ApiStatus status, Class<?> clazz, T data) {
        return new ApiResponse<>(status, List.of(status.message() + "." + clazz.getSimpleName()), data);
    }

    public static <T> ApiResponse<T> badRequest(BindException e) {
        return new ApiResponse<>(BAD_REQUEST, collectBindExceptionReasons(e), null);
    }

    public static <T> ApiResponse<T> badRequest(ConstraintViolationException e) {
        return new ApiResponse<>(BAD_REQUEST, collectConstraintViolationExceptionReasons(e), null);
    }

    private static List<String> collectBindExceptionReasons(BindException e) {
        return e.getFieldErrors().stream().map(fe -> fe.getCode() + "." + fe.getField()).toList();
    }

    private static List<String> collectConstraintViolationExceptionReasons(ConstraintViolationException e) {
        return e.getConstraintViolations().stream().map(cve -> {
            String property = cve.getPropertyPath().toString();
            String field = property.substring(property.contains("[") ? property.indexOf("[") : property.indexOf("."));
            String anno = cve.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            return anno + ".request" + field;
        }).toList();
    }
}
