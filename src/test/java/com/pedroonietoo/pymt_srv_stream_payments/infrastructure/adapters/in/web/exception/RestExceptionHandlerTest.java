package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception;

import com.pedroonietoo.pymt_srv_stream_payments.domain.exceptions.PaymentNotFoundException;
import com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception.response.ApiErrorResponse;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {

    private RestExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        exceptionHandler = new RestExceptionHandler();
    }

    @Test
    void shouldHandleMethodArgumentNotValid() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getErrorCount()).thenReturn(2);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("object", "field", "message")));
        when(bindingResult.getGlobalErrors()).thenReturn(List.of(new ObjectError("object", "global message")));

        org.springframework.core.MethodParameter methodParameter =
                new org.springframework.core.MethodParameter(RestExceptionHandlerTest.class.getDeclaredMethod("dummyMethod"), -1);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(ex, headers, HttpStatus.BAD_REQUEST, webRequest);

        assertApiError(response, HttpStatus.BAD_REQUEST, "Validation failed for provided fields");
    }

    private void dummyMethod() { }

    @Test
    void shouldHandleHttpRequestMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST", List.of("GET"));
        ResponseEntity<Object> response = exceptionHandler.handleHttpRequestMethodNotSupported(ex, headers, HttpStatus.METHOD_NOT_ALLOWED, webRequest);

        assertApiError(response, HttpStatus.METHOD_NOT_ALLOWED, "HTTP method 'POST' is not supported for this endpoint. Supported methods: [GET]");
    }

    @Test
    void shouldHandleNoResourceFoundException() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        when(ex.getResourcePath()).thenReturn("invalid/path");
        ResponseEntity<Object> response = exceptionHandler.handleNoResourceFoundException(ex, headers, HttpStatus.NOT_FOUND, webRequest);

        assertApiError(response, HttpStatus.NOT_FOUND, "Resource not found for path '/invalid/path'");
    }

    @Test
    void shouldHandleHttpMessageNotReadable() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error", mock(HttpInputMessage.class));
        ResponseEntity<Object> response = exceptionHandler.handleHttpMessageNotReadable(ex, headers, HttpStatus.BAD_REQUEST, webRequest);

        assertApiError(response, HttpStatus.BAD_REQUEST, "Malformed JSON request or invalid data type");
    }

    @Test
    void shouldHandleHttpMediaTypeNotSupported() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(MediaType.APPLICATION_XML, List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<Object> response = exceptionHandler.handleHttpMediaTypeNotSupported(ex, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE, webRequest);

        assertApiError(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Media type 'application/xml' is not supported. Supported media types: [application/json]");
    }

    @Test
    void shouldHandleMissingRequestHeader() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        when(methodParameter.getNestedParameterType()).thenAnswer(inv -> String.class);

        MissingRequestHeaderException ex = new MissingRequestHeaderException("X-Header", methodParameter);

        ResponseEntity<Object> response = exceptionHandler.handleMissingRequestHeader(ex);

        assertApiError(response, HttpStatus.BAD_REQUEST, "Required header 'X-Header' is missing");
    }

    @Test
    void shouldHandleMissingServletRequestParameter() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("queryParam", "String");
        ResponseEntity<Object> response = exceptionHandler.handleMissingServletRequestParameter(ex, headers, HttpStatus.BAD_REQUEST, webRequest);

        assertApiError(response, HttpStatus.BAD_REQUEST, "Required query parameter 'queryParam' is missing");
    }

    @Test
    void shouldHandleDomainInvariants() {
        ResponseEntity<Object> responseNpe = exceptionHandler.handleDomainInvariants(new NullPointerException("Null error"));
        assertApiError(responseNpe, HttpStatus.BAD_REQUEST, "Invalid or null argument in domain rule");
        ResponseEntity<Object> responseIae = exceptionHandler.handleDomainInvariants(new IllegalArgumentException("Arg error"));

        assertApiError(responseIae, HttpStatus.BAD_REQUEST, "Invalid or null argument in domain rule");
    }

    @Test
    void shouldHandleIllegalState() {
        ResponseEntity<Object> response = exceptionHandler.handleIllegalState(new IllegalStateException("State error"));

        assertApiError(response, HttpStatus.UNPROCESSABLE_ENTITY, "Invalid state transition or operation");
    }

    @Test
    void shouldHandleDataIntegrity() {
        ResponseEntity<Object> response = exceptionHandler.handleDataIntegrity(new DataIntegrityViolationException("Constraint error"));

        assertApiError(response, HttpStatus.CONFLICT, "Data integrity conflict");
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException("value", String.class, "paramName", mock(MethodParameter.class), new IllegalArgumentException());
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentTypeMismatch(ex);

        assertApiError(response, HttpStatus.BAD_REQUEST, "Parameter 'paramName' with value 'value' could not be converted to type 'String'");
    }

    @Test
    void shouldHandleGenericException() {
        ResponseEntity<Object> response = exceptionHandler.handleGenericException(new Exception("Generic error"));

        assertApiError(response, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal server error occurred");
    }

    @Test
    void shouldHandlePaymentNotFoundException() {
        PaymentNotFoundException ex = new PaymentNotFoundException(UUID.randomUUID());
        ResponseEntity<Object> response = exceptionHandler.handlePaymentNotFoundException(ex);

        assertApiError(response, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @Test
    void shouldHandleRateLimitException() {
        RequestNotPermitted ex = mock(RequestNotPermitted.class);
        ResponseEntity<Object> response = exceptionHandler.handleRateLimit(ex);

        assertApiError(response, HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded. Too many requests, please try again later.");
    }

    private void assertApiError(ResponseEntity<Object> response, HttpStatus expectedStatus, String expectedMessage) {
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatusCode());

        ApiErrorResponse apiError = (ApiErrorResponse) response.getBody();
        assertNotNull(apiError);
        assertEquals(expectedStatus, apiError.getStatus());
        assertEquals(expectedMessage, apiError.getMessage());
        assertNotNull(apiError.getSubErrors());
    }
}