package ru.practicum.error.model;

public class InitiatorRequestException extends RuntimeException {
    public InitiatorRequestException(String message) {
        super(message);
    }
}