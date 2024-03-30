package psam.portfolio.sunder.english.global.api;

import jakarta.validation.ConstraintViolation;
import org.springframework.validation.FieldError;

public class ApiReasonDetail {

    private final String reason;
    private final String message;

    public ApiReasonDetail(FieldError fieldError) {
        this.reason = fieldError.getCode() + "." + fieldError.getField();
        if ("typeMismatch".equalsIgnoreCase(fieldError.getCode())) {
            this.message = fieldError.getRejectedValue() + " has occurred type mismatch exception.";
        } else {
            this.message = fieldError.getDefaultMessage();
        }
    }

    public ApiReasonDetail(ConstraintViolation<?> violation) {
        String property = violation.getPropertyPath().toString();
        String field = property.substring(property.contains("[") ? property.indexOf("[") : property.indexOf("."));
        String anno = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
        this.reason = anno + ".request" + field;
        this.message = violation.getMessage();
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }
}
