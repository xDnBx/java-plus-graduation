package ru.practicum.exception;

public class RequestLimitException extends RuntimeException {
    public RequestLimitException(String message) {
        super(message);
    }
}