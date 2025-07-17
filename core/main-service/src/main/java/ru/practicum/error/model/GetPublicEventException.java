package ru.practicum.error.model;

public class GetPublicEventException extends RuntimeException {
    public GetPublicEventException(String message) {
        super(message);
    }
}