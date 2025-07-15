package ru.practicum.explore_with_me.category.service;

import ru.practicum.explore_with_me.category.dto.CategoryDto;

import java.util.Collection;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    void deleteCategoryById(Long categoryId);

    Collection<CategoryDto> getCategories(int from, int size);

    CategoryDto updateCategory(CategoryDto request, Long categoryId);

    CategoryDto getCategoryById(Long categoryId);
}
