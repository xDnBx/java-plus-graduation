package ru.practicum.event.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateTimeValidator.class)
public @interface EventDateTime {
    String message() default "DateTime should be no earlier than 2 hours from now";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}