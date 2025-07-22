package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MergeCommentRequest {
    @NotNull(message = "Event id is required")
    Long eventId;

    @NotBlank(message = "Text is required")
    String text;

    @Builder.Default
    LocalDateTime publishedOn = LocalDateTime.now();
}