package ru.practicum.explore_with_me.error.model;

public class NotPublishedEventRequestException extends RuntimeException {
    public NotPublishedEventRequestException(String message) {
        super(message);
    }
}
