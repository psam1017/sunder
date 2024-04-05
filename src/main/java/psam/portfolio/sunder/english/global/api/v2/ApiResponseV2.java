package psam.portfolio.sunder.english.global.api.v2;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static psam.portfolio.sunder.english.global.api.v1.ApiStatus.BAD_REQUEST;
import static psam.portfolio.sunder.english.global.api.v1.ApiStatus.OK;

@Getter
public class ApiResponseV2<T> {

    private final String code;
    private final String message;
    private final List<String> reasons;
    private final T data;

    private ApiResponseV2(ApiStatus apiStatus, List<String> reasons, T data){
        this.code = apiStatus.code();
        this.message = apiStatus.message();
        this.reasons = reasons;
        this.data = data;
    }

    public static <T> ApiResponseV2<T> of(ApiStatus status, T data) {
        return new ApiResponseV2<>(status, List.of(status.message()), data);
    }

    public static <T> ApiResponseV2<T> ok(T data) {
        return new ApiResponseV2<>(OK, List.of(OK.message()), data);
    }

    public static <T> ApiResponseV2<T> error(ApiStatus status, Class<?> clazz, String concrete, T data) {
        return new ApiResponseV2<>(status, List.of(status.message() + "." + clazz.getSimpleName() + "." + concrete), data);
    }

    public static <T> ApiResponseV2<T> error(ApiStatus status, Class<?> clazz, T data) {
        return new ApiResponseV2<>(status, List.of(status.message() + "." + clazz.getSimpleName()), data);
    }

    public static ApiResponseV2<Object> badRequest(BindException e) {
        return fromBindException(e);
    }

    public static ApiResponseV2<Object> badRequest(ConstraintViolationException e) {
        return fromConstraintViolationException(e);
    }

    private static ApiResponseV2<Object> fromBindException(BindException e) {
        List<String> reasons = new ArrayList<>();
        List<ApiReasonDetail> details = new ArrayList<>();
        for (FieldError fieldError : e.getFieldErrors()) {
            String reason = fieldError.getCode() + "." + fieldError.getField();
            reasons.add(reason);
            details.add(new ApiReasonDetail(fieldError));
        }
        return new ApiResponseV2<>(BAD_REQUEST, reasons, Map.of("details", details));
    }

    private static ApiResponseV2<Object> fromConstraintViolationException(ConstraintViolationException e) {
        List<String> reasons = new ArrayList<>();
        List<ApiReasonDetail> details = new ArrayList<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            String property = violation.getPropertyPath().toString();
            String field = property.substring(property.contains("[") ? property.indexOf("[") : property.indexOf("."));
            String anno = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            reasons.add(anno + ".request" + field);
            details.add(new ApiReasonDetail(violation, field, anno));
        }
        return new ApiResponseV2<>(BAD_REQUEST, reasons, Map.of("details", details));
    }
}
