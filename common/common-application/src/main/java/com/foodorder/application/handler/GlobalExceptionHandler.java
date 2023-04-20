package com.foodorder.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected error!")
                .build();
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(ValidationException exception) {
        ErrorDTO errorDTO;
        if (exception instanceof ConstraintViolationException) {
            String violations = extractViolationsFromException((ConstraintViolationException) exception);
            log.error(violations, exception);
            errorDTO = ErrorDTO.builder()
                    .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(violations)
                    .build();
        } else {
            String exceptionMsg = exception.getMessage();
            log.error(exceptionMsg, exception);
            errorDTO = ErrorDTO.builder()
                    .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(exceptionMsg)
                    .build();
        }

        return errorDTO;
    }

    private String extractViolationsFromException(ConstraintViolationException exception) {
        return exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("--"));
    }
}
