package ru.practicum.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserResponse;

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