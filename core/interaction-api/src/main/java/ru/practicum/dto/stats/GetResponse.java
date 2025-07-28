package ru.practicum.dto.stats;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetResponse {
    @NotBlank
    String app;

    @NotBlank
    String uri;

    long hits;
}