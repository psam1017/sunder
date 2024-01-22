package psam.portfolio.sunder.english.global.advice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
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
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;

import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

/**
 * -- Caution --
 * ResponseEntityExceptionHandler basically returns a ResponseEntity with org.springframework.http.ProblemDetail, Representation for an RFC 7807 problem details in the body.
 * if you extend ResponseEntityExceptionHandler and return a custom problem details in the body instead of ProblemDetail, You may not standardize your API as Http API.
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper;

    /**
     * You may need to return body in ResponseEntity as String, if an Exception is instance of ErrorResponse(Not Always).
     * refer to 'writeWithMessageConverters' method in org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor to find out the reason.
     */
    private String writeJson(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Failed to create error messages.";
        }
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[HttpRequestMethodNotSupportedException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = ex.getMethod() + " method is not supported for this request. Supported methods are " + ex.getSupportedHttpMethods();
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.METHOD_NOT_ALLOWED, message);

        return createResponseEntity(body, METHOD_NOT_ALLOWED);
    }

    private ResponseEntity<Object> createResponseEntity(ApiResponse<Object> body, HttpStatus status) {
        return new ResponseEntity<>(writeJson(body), status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[HttpMediaTypeNotSupportedException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = ex.getContentType() + " media type is not supported. Supported media types are " + ex.getSupportedMediaTypes();
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.UNSUPPORTED_MEDIA_TYPE, message);

        return createResponseEntity(body, UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[HttpMediaTypeNotAcceptableException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = "Unable to negotiate. Acceptable media types are " + ex.getSupportedMediaTypes();
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.NOT_ACCEPTABLE, message);

        return createResponseEntity(body, NOT_ACCEPTABLE);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[MissingPathVariableException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = "required path variable '" + ex.getVariableName() + "' is missing.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.BAD_REQUEST, message);

        return createResponseEntity(body, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[MissingServletRequestParameterException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = "Required parameter '" + ex.getParameterName() + "' is missing.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.BAD_REQUEST, message);

        return createResponseEntity(body, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[MissingServletRequestPartException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = "Required part '" + ex.getRequestPartName() + "' is missing.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.BAD_REQUEST, message);

        return createResponseEntity(body, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[ServletRequestBindingException handle] request uri = {}", ((ServletWebRequest)request).getRequest().getRequestURI());
        ex.printStackTrace();

        String message = "Server requires the request to be conditional. You may be missing a required precondition header, such as 'If-Match'";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.PRECONDITION_REQUIRED, message);

        return createResponseEntity(body, PRECONDITION_REQUIRED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[MethodArgumentNotValidException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        ApiResponse<Object> body = ApiResponse.badRequest(ex);
        return createResponseEntity(body, BAD_REQUEST);
    }

    /**
     * It may be bacause you didn't use @Valid, or BindingResult. Check the handler that raised this exception.
     */
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[HandlerMethodValidationException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());
        ex.printStackTrace();

        String message = "Unexpected exception thrown in Server Handler.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.INTERNAL_SERVER_ERROR, message);

        return createResponseEntity(body, INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[NoHandlerFoundException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = "There is no API(or handler) for " + ex.getHttpMethod() + " /" + ex.getRequestURL();
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.NOT_FOUND, message);

        return createResponseEntity(body, NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[NoResourceFoundException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());
        System.out.println(ex.getMessage());

        String message = "There is no API(or resource) for " + ex.getHttpMethod() + " /" + ex.getResourcePath();
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.NOT_FOUND, message);

        return createResponseEntity(body, NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[AsyncRequestTimeoutException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = "Service Unavailable. Please retry later.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.SERVICE_UNAVAILABLE, message);

        return createResponseEntity(body, SERVICE_UNAVAILABLE);
    }

    @Override
    protected ResponseEntity<Object> handleErrorResponseException(ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[ErrorResponseException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());
        ex.printStackTrace();

        String message = "Unexpected exception thrown.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.INTERNAL_SERVER_ERROR, message);

        return createResponseEntity(body, INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[MaxUploadSizeExceededException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = "You exceeded max upload size. max = " + ex.getMaxUploadSize() / 1024 / 1024 + "MB";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.BAD_REQUEST, message);

        return createResponseEntity(body, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[ConversionNotSupportedException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());
        ex.printStackTrace();

        String message = "Unexpected exception thrown.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.INTERNAL_SERVER_ERROR, message);

        return createResponseEntity(body, INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[TypeMismatchException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = ex.getPropertyName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.BAD_REQUEST, message);

        return createResponseEntity(body, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[HttpMessageNotReadableException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());

        String message = "Unable to read http message. Please check your request body.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.BAD_REQUEST, message);

        return createResponseEntity(body, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.error("[HttpMessageNotWritableException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());
        ex.printStackTrace();

        String message = "Unexpected exception thrown.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.INTERNAL_SERVER_ERROR, message);

        return createResponseEntity(body, INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        log.error("[MethodValidationException handle] request uri = " + ((ServletWebRequest)request).getRequest().getRequestURI());
        ex.printStackTrace();

        String message = "Unexpected exception thrown.";
        ApiResponse<Object> body = ApiResponse.of(ApiStatus.INTERNAL_SERVER_ERROR, message);

        return createResponseEntity(body, INTERNAL_SERVER_ERROR);
    }
}
