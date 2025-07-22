package ru.practicum.dto.compilations;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.EventShortDto;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationResponse {
    Long id;
    String title;
    Boolean pinned;
    Set<EventShortDto> events;
}