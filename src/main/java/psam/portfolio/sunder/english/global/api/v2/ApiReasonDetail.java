package psam.portfolio.sunder.english.global.api.v2;

import jakarta.validation.ConstraintViolation;
import org.springframework.validation.FieldError;

public class ApiReasonDetail {

    private final String reason;
    private final String message;

    public ApiReasonDetail(FieldError fieldError) {
        this.reason = fieldError.getCode() + "." + fieldError.getField();
        message = resolveErrorMessage(fieldError);
    }

    public ApiReasonDetail(ConstraintViolation<?> violation, String field, String annotation) {
        this.reason = annotation + ".request" + field;
        this.message = violation.getMessage();
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }

    private String resolveErrorMessage(FieldError fieldError) {
        if ("typeMismatch".equalsIgnoreCase(fieldError.getCode())) {
            return fieldError.getRejectedValue() + " has occurred type mismatch exception.";
        }
        return message;
    }
}
