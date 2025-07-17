package ru.practicum.error.model;

public class PublicationException extends RuntimeException {
    public PublicationException(String message) {
        super(message);
    }
}