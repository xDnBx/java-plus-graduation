package ru.practicum.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.enums.SortType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAllEventsPublicParams {
    String text;
    Set<Long> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Boolean onlyAvailable;
    SortType sort;
    Integer from;
    Integer size;
}