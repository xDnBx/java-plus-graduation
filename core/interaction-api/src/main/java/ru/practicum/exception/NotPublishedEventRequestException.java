package ru.practicum.exception;

public class NotPublishedEventRequestException extends RuntimeException {
    public NotPublishedEventRequestException(String message) {
        super(message);
    }
}