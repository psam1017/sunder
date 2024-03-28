package psam.portfolio.sunder.english.global.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
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

    public static ApiResponse<Object> badRequest(BindException e) {
        return fromBindException(e);
    }

    public static ApiResponse<Object> badRequest(ConstraintViolationException e) {
        return fromConstraintViolationException(e);
    }

    private static ApiResponse<Object> fromBindException(BindException e) {
        List<String> reasons = new ArrayList<>();
        List<ApiReasonDetail> data = new ArrayList<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            String reason = fieldError.getCode() + "." + fieldError.getField();
            reasons.add(reason);
            data.add(new ApiReasonDetail(fieldError));
        }
        return new ApiResponse<>(BAD_REQUEST, reasons, data);
    }

    private static ApiResponse<Object> fromConstraintViolationException(ConstraintViolationException e) {
        List<String> reasons = new ArrayList<>();
        List<ApiReasonDetail> data = new ArrayList<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String property = violation.getPropertyPath().toString();
            String field = property.substring(property.contains("[") ? property.indexOf("[") : property.indexOf("."));
            String anno = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            reasons.add(anno + ".request" + field);
            data.add(new ApiReasonDetail(violation));
        }
        return new ApiResponse<>(BAD_REQUEST, reasons, data);
    }
}
