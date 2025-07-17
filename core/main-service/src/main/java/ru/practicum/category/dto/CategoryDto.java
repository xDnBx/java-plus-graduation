package ru.practicum.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CategoryDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 50, message = "Name length must be between 1 and 50 characters")
    String name;
}