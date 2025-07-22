package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.enums.EventStateAction;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminPatchEventDto {
    @Size(min = 20, max = 2000, message = "Annotation should be between 20 and 2000 characters long")
    String annotation;

    Long category;

    @Size(min = 20, max = 7000, message = "Description should be between 20 and 7000 characters long")
    String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    EventStateAction stateAction;

    @Size(min = 3, max = 120, message = "Title should be between 3 and 120 characters long")
    String title;
}