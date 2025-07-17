package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

@Mapper
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category requestToCategory(CategoryDto categoryDto);

    CategoryDto categoryToResponse(Category category);
}