package ru.practicum.error.model;

public class RequestLimitException extends RuntimeException {
    public RequestLimitException(String message) {
        super(message);
    }
}