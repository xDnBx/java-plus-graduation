package ru.practicum.dto.comment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserResponse;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    Long id;
    String text;
    UserResponse author;
    LocalDateTime publishedOn;
    EventShortDto event;
}