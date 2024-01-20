package portfolio.sunder.global.advice.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import portfolio.sunder.global.api.ApiException;
import portfolio.sunder.global.api.ApiResponse;
import portfolio.sunder.global.api.ApiStatus;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice(basePackages = "portfolio.sunder.web")
public class GlobalControllerAdvice {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException ex, HttpServletRequest request) {

        log.error("[ApiException handle] request uri = {}", request.getRequestURI());

        return new ResponseEntity<>(ex.getResponse(), OK);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException ex, HttpServletRequest request) {

        log.error("[BindException handle] request uri = {}", request.getRequestURI());
        log.error("[reasons] {}", ex.toString());

        return new ResponseEntity<>(ApiResponse.badRequest(ex), BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {

        log.error("[ConstraintViolationException handle] request uri = {}", request.getRequestURI());
        log.error("[reasons] {}", ex.toString());

        return new ResponseEntity<>(ApiResponse.badRequest(ex), BAD_REQUEST);
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
