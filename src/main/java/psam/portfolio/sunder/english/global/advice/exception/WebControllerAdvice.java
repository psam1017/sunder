package psam.portfolio.sunder.english.global.advice.exception;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.web.user.model.entity.User;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class WebControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException ex, HttpServletRequest request) {

        log.error("[ApiException handle] request uri = {}", request.getRequestURI());
        log.error("[reasons] {}", ex.getResponse().getReasons().toString());

        return new ResponseEntity<>(ex.getResponse(), BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException ex, HttpServletRequest request) {

        log.error("[BindException handle] request uri = {}", request.getRequestURI());
        log.error("[reasons] {}", ex.toString());

        ApiResponse<Object> body = ApiResponse.badRequest(ex);

        return new ResponseEntity<>(body, BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {

        log.error("[ConstraintViolationException handle] request uri = {}", request.getRequestURI());
        log.error("[reasons] {}", ex.toString());

        ApiResponse<Object> body = ApiResponse.badRequest(ex);

        return new ResponseEntity<>(body, BAD_REQUEST);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<Object> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex, HttpServletRequest request) {

        log.error("[UnrecognizedPropertyException handle] request uri = {}", request.getRequestURI());
        ex.printStackTrace();

        String message = ex.getPropertyName() + " is an unrecognized property.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.BAD_REQUEST, message);

        return new ResponseEntity<>(body, BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex, HttpServletRequest request) {

        log.error("[InvalidFormatException handle] request uri = {}", request.getRequestURI());
        ex.printStackTrace();

        String message = ex.getValue() + " has invalid format.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.BAD_REQUEST, message);

        return new ResponseEntity<>(body, BAD_REQUEST);
    }

    @ExceptionHandler(JacksonException.class)
    public ResponseEntity<ApiResponse<Object>> handleJacksonException(JacksonException ex, HttpServletRequest request) {

        log.error("[JacksonException handle] request uri = {}", request.getRequestURI());
        ex.printStackTrace();

        String message = "Unable to process json.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.BAD_REQUEST, message);

        return new ResponseEntity<>(body,  BAD_REQUEST);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(MultipartException ex, HttpServletRequest request) {

        log.error("[MultipartException handle] request uri = {}", request.getRequestURI());
        ex.printStackTrace();

        String message = "Unable to complete the multipart request. Or, You may be missing requeired parts.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.INTERNAL_SERVER_ERROR, message);

        return new ResponseEntity<>(body, INTERNAL_SERVER_ERROR);
    }
}
