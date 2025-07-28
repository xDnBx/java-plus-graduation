package ru.practicum.exception;

public class InitiatorRequestException extends RuntimeException {
    public InitiatorRequestException(String message) {
        super(message);
    }
}