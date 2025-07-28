package ru.practicum.dto.compilations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    @NotBlank(groups = OnCreate.class, message = "title is required")
    @Size(groups = {OnCreate.class, OnUpdate.class}, min = 1, max = 50, message = "title length must be between 1 and 50 characters")
    String title;

    @Builder.Default
    Boolean pinned = false;

    Set<Long> events;

    public interface OnCreate {}  // Группа для создания

    public interface OnUpdate {}  // Группа для обновления
}