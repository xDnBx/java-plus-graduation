package ru.practicum.exception;

public class StatsClientException extends RuntimeException {
    public StatsClientException(Integer code, String message) {
        super("StatusCode = " + code + "\nMessage = " + message);
    }
}