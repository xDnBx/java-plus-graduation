package ru.practicum.exception;

public class UpdateStartDateException extends RuntimeException {
    public UpdateStartDateException(String message) {
        super(message);
    }
}