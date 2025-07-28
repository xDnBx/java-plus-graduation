package ru.practicum.exception;

public class AlreadyConfirmedException extends RuntimeException {
    public AlreadyConfirmedException(String message) {
        super(message);
    }
}