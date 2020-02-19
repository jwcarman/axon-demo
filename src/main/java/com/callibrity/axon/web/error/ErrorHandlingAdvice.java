package com.callibrity.axon.web.error;

import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandlingAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<Object> onValidationError(JSR303ViolationException exception, WebRequest request) {
        final List<String> errors = exception.getViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        final ErrorResponse body = ErrorResponse.builder()
                .errors(errors)
                .build();
        return handleExceptionInternal(exception, body, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, request);
    }
}
