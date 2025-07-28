package ru.practicum.dto.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class HitRequest {
    @NotBlank
    String app;

    @NotBlank
    String uri;

    @NotBlank
    String ip;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();
}