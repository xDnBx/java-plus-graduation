package ru.practicum.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryService;
import ru.practicum.dto.category.CategoryDto;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCategoryController {
    final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CategoryDto> getCategories(@PositiveOrZero(message = "from must be greater or equal to zero")
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive(message = "size must be greater than zero")
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable(name = "catId") Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }
}