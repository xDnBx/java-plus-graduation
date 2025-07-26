package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.model.Category;
import ru.practicum.dto.category.CategoryDto;

@Mapper
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category toCategory(CategoryDto categoryDto);

    CategoryDto toDto(Category category);
}