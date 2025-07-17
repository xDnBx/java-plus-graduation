package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateUserRequest {
    @NotNull(message = "Email is required")
    @Email(message = "Email is not valid")
    @Size(min = 6, max = 254, message = "Email should be between 6 and 254 characters long")
    String email;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 250, message = "Name should be between 2 and 250 characters long")
    String name;
}