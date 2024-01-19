package portfolio.sunder.global.advice.controller;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import portfolio.sunder.global.api.ApiResponse;
import portfolio.sunder.global.api.ApiStatus;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@SuppressWarnings("ALL")
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    // TODO: 2024-01-20 log.error
    // https://www.baeldung.com/exception-handling-for-rest-with-spring

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String message = new StringBuilder()
                .append(ex.getMethod())
                .append(" method is not supported for this request. Supported methods are ")
                .append(ex.getSupportedHttpMethods().toString())
                .toString();

        return new ResponseEntity<>(ApiResponse.of(ApiStatus.METHOD_NOT_ALLOWED, message), new HttpHeaders(), METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String message = new StringBuilder()
                .append(ex.getContentType())
                .append(" media type is not supported. Supported media types are ")
                .append(ex.getSupportedMediaTypes().toString())
                .toString();

        return new ResponseEntity<>(ApiResponse.of(ApiStatus.UNSUPPORTED_MEDIA_TYPE, message), new HttpHeaders(), UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleHttpMediaTypeNotAcceptable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleMissingPathVariable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String message = new StringBuilder()
                .append("Required parameter '")
                .append(ex.getParameterName())
                .append("' is missing.")
                .toString();

        return new ResponseEntity<>(ApiResponse.of(ApiStatus.BAD_REQUEST, message), new HttpHeaders(), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String message = new StringBuilder()
                .append("Required part '")
                .append(ex.getRequestPartName())
                .append("' is missing.")
                .toString();

        return new ResponseEntity<>(ApiResponse.of(ApiStatus.BAD_REQUEST, message), new HttpHeaders(), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleServletRequestBindingException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + " : " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + " : " + error.getDefaultMessage());
        }

        ApiResponse<Object> response = ApiResponse.of(ApiStatus.BAD_REQUEST, errors);
        return handleExceptionInternal(ex, response, headers, BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleHandlerMethodValidationException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String message = new StringBuilder()
                .append("No handler found for ")
                .append(ex.getHttpMethod())
                .append(" ")
                .append(ex.getRequestURL())
                .toString();

        return new ResponseEntity<Object>(ApiResponse.of(ApiStatus.NOT_FOUND, message), new HttpHeaders(), NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleNoResourceFoundException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleAsyncRequestTimeoutException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleErrorResponseException(ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleErrorResponseException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleMaxUploadSizeExceededException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleConversionNotSupported(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String message = new StringBuilder()
                .append(ex.getPropertyName())
                .append(" should be of type ")
                .append(ex.getRequiredType().getName())
                .toString();

        return new ResponseEntity<Object>(ApiResponse.of(ApiStatus.BAD_REQUEST, message), new HttpHeaders(), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleHttpMessageNotWritable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return super.handleMethodValidationException(ex, headers, status, request);
    }
}
