package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception;

import com.pedroonietoo.pymt_srv_stream_payments.domain.exceptions.PaymentNotFoundException;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception.response.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger TECHNICAL_LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        var apiError = new ApiErrorResponse(BAD_REQUEST);
        apiError.setMessage("Validation failed for provided fields");
        apiError.setDetailedMessage(String.format("%d validation error(s) found", ex.getBindingResult().getErrorCount()));
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addGlobalValidationErrors(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(apiError, ex);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        String message = String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                ex.getMethod(), supportedMethods != null ? supportedMethods : "none");
        var apiError = new ApiErrorResponse(METHOD_NOT_ALLOWED, message, ex);
        apiError.addSubError("METHOD_NOT_ALLOWED", message);
        return buildResponseEntity(apiError, ex);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String message = String.format("Resource not found for path '/%s'", ex.getResourcePath());
        var apiError = new ApiErrorResponse(NOT_FOUND, message, ex);
        apiError.addSubError("RESOURCE_NOT_FOUND", message);
        return buildResponseEntity(apiError, ex);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        var apiError = new ApiErrorResponse(BAD_REQUEST, "Malformed JSON request or invalid data type", ex);
        apiError.addSubError("MALFORMED_JSON", "Verify request body syntax and valid enum values");
        return buildResponseEntity(apiError, ex);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String message = String.format("Media type '%s' is not supported. Supported media types: %s",
                ex.getContentType(), ex.getSupportedMediaTypes());
        var apiError = new ApiErrorResponse(UNSUPPORTED_MEDIA_TYPE, message, ex);
        apiError.addSubError("UNSUPPORTED_MEDIA_TYPE", message);
        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Object> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        String message = String.format("Required header '%s' is missing", ex.getHeaderName());
        var apiError = new ApiErrorResponse(BAD_REQUEST, message, ex);
        apiError.addSubError("MISSING_HEADER", message);
        return buildResponseEntity(apiError, ex);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String message = String.format("Required query parameter '%s' is missing", ex.getParameterName());
        var apiError = new ApiErrorResponse(BAD_REQUEST, message, ex);
        apiError.addSubError("MISSING_QUERY_PARAM", message);
        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleDomainInvariants(RuntimeException ex) {
        var apiError = new ApiErrorResponse(BAD_REQUEST, "Invalid or null argument in domain rule", ex);
        apiError.addSubError("INVALID_ARGUMENT", ex.getMessage());
        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        var apiError = new ApiErrorResponse(UNPROCESSABLE_ENTITY, "Invalid state transition or operation", ex);
        apiError.addSubError("INVALID_STATE_TRANSITION", ex.getMessage());
        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex) {
        var apiError = new ApiErrorResponse(CONFLICT, "Data integrity conflict", ex);
        apiError.addSubError("DUPLICATE_RESOURCE", "Entity already exists or violated a unique constraint");
        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Parameter '%s' with value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), requiredType);
        var apiError = new ApiErrorResponse(BAD_REQUEST, message, ex);
        apiError.addSubError("TYPE_MISMATCH", message);
        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        var apiError = new ApiErrorResponse(INTERNAL_SERVER_ERROR, "An unexpected internal server error occurred", ex);
        apiError.addSubError("INTERNAL_ERROR", "Please contact support or try again later");
        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Object> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        var apiError = new ApiErrorResponse(NOT_FOUND, ex.getMessage(), ex);
        apiError.addSubError("PAYMENT_NOT_FOUND", "The requested payment could not be found");
        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(io.github.resilience4j.ratelimiter.RequestNotPermitted.class)
    public ResponseEntity<Object> handleRateLimit(io.github.resilience4j.ratelimiter.RequestNotPermitted ex) {
        var apiError = new ApiErrorResponse(
                org.springframework.http.HttpStatus.TOO_MANY_REQUESTS,
                "Rate limit exceeded. Too many requests, please try again later.",
                ex
        );
        apiError.addSubError("RATE_LIMIT_EXCEEDED", "You have reached the maximum allowed request rate.");
        return buildResponseEntity(apiError, ex);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiErrorResponse apiError, Exception ex) {
        TECHNICAL_LOGGER.error("Exception handled [Status: {} | ErrorCode: {} | Message: {}]",
                apiError.getStatus(), apiError.getErrorCode(), apiError.getMessage(), ex);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}