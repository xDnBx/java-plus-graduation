package ru.practicum.error.model;

public class NotPublishedEventRequestException extends RuntimeException {
    public NotPublishedEventRequestException(String message) {
        super(message);
    }
}