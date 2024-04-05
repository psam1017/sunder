package psam.portfolio.sunder.english.global.api.v1;

import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.validation.BindException;

import java.util.List;

import static psam.portfolio.sunder.english.global.api.v1.ApiStatus.BAD_REQUEST;
import static psam.portfolio.sunder.english.global.api.v1.ApiStatus.OK;

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

    public static ApiResponse<Object> badRequest(BindException e) {
        return new ApiResponse<>(
                BAD_REQUEST,
                e.getFieldErrors().stream().map(fieldError -> fieldError.getCode() + "." + fieldError.getField()).toList(),
                null
        );
    }

    public static ApiResponse<Object> badRequest(ConstraintViolationException e) {
        return new ApiResponse<>(
                BAD_REQUEST,
                e.getConstraintViolations().stream().map(violation -> {
                    String property = violation.getPropertyPath().toString();
                    String field = property.substring(property.contains("[") ? property.indexOf("[") : property.indexOf("."));
                    String anno = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
                    return anno + ".request" + field;
                }).toList(),
                null
        );
    }
}
