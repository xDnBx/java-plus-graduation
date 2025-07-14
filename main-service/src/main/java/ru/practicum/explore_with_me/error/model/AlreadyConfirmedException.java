package ru.practicum.explore_with_me.error.model;

public class AlreadyConfirmedException extends RuntimeException {
    public AlreadyConfirmedException(String message) {
        super(message);
    }
}