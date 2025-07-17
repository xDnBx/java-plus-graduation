package ru.practicum.error.model;

public class UpdateStartDateException extends RuntimeException {
    public UpdateStartDateException(String message) {
        super(message);
    }
}