package ru.practicum.expore_with_me.controller;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.expore_with_me.model.ErrorResponse;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.error("BAD_REQUEST", e);
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("BAD_REQUEST", e);
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(MethodArgumentNotValidException e) {
        log.error("BAD_REQUEST", e);
        return ErrorResponse.builder()
                .error(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("BAD_REQUEST", e);
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("BAD_REQUEST", e);
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("INTERNAL_SERVER_ERROR", e);
        return ErrorResponse.builder()
                .error(e.getMessage())
                .timestamp(LocalDateTime.now())
                .status(500)
                .build();
    }
}
