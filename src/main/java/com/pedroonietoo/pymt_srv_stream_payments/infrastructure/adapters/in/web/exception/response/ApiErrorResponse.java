package com.pedroonietoo.pymt_srv_stream_payments.infrastructure.adapters.in.web.exception.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("apiError")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonPropertyOrder({
        "timestamp",
        "errorCode",
        "message",
        "detailedMessage",
        "status",
        "subErrors"
})
public class ApiErrorResponse {

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private final LocalDateTime timestamp;

    @JsonProperty("errorCode")
    private final Integer errorCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("detailedMessage")
    private String detailedMessage;

    @JsonProperty("status")
    private final HttpStatus status;

    @JsonProperty("subErrors")
    private final List<ApiSubError> subErrors;

    public ApiErrorResponse(HttpStatus status) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.errorCode = status.value();
        this.subErrors = new ArrayList<>();
    }

    public ApiErrorResponse(HttpStatus status, String message, String detailedMessage) {
        this(status);
        this.message = message;
        this.detailedMessage = detailedMessage;
    }

    public ApiErrorResponse(HttpStatus status, String message, Throwable ex) {
        this(status, message, ex != null ? ex.getLocalizedMessage() : null);
    }

    public void addSubError(String code, String message) {
        this.subErrors.add(new ApiSubError(code, message));
    }

    public void addValidationErrors(List<FieldError> fieldErrors) {
        fieldErrors.forEach(error -> addSubError(error.getField(), error.getDefaultMessage()));
    }

    public void addGlobalValidationErrors(List<ObjectError> globalErrors) {
        globalErrors.forEach(error -> addSubError(error.getObjectName(), error.getDefaultMessage()));
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public Integer getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDetailedMessage() { return detailedMessage; }
    public void setDetailedMessage(String detailedMessage) { this.detailedMessage = detailedMessage; }
    public HttpStatus getStatus() { return status; }
    public List<ApiSubError> getSubErrors() { return subErrors; }
}