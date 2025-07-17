package ru.practicum.event.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EventDateTimeValidator implements ConstraintValidator<EventDateTime, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext context) {
        return dateTime == null || dateTime.isAfter(LocalDateTime.now().plusHours(2));
    }
}