package ru.practicum.expore_with_me.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private final String error;
    private final LocalDateTime timestamp;
    private final int status;
}
