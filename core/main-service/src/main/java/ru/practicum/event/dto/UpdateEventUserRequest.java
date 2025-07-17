package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.enums.EventStateAction;
import ru.practicum.event.validation.EventDateTime;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000, message = "Annotation should be between 20 and 2000 characters long")
    String annotation;

    @Size(min = 20, max = 7000, message = "Description should be between 20 and 7000 characters long")
    String description;

    @EventDateTime
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @Size(min = 3, max = 120, message = "Title should be between 3 and 120 characters long")
    String title;

    Long category;
    LocationDto location;
    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;
    EventStateAction stateAction;
}