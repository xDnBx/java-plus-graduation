package ru.practicum.explore_with_me.category.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.category.dao.CategoryRepository;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.mapper.CategoryMapper;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.service.CategoryService;
import ru.practicum.explore_with_me.error.model.NotFoundException;

import java.util.Collection;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(CategoryDto categoryMergeRequest) {
        Category category = categoryMapper.requestToCategory(categoryMergeRequest);
        CategoryDto categoryResponse = categoryMapper.categoryToResponse(categoryRepository.save(category));
        log.info("Category with id={} was created", categoryResponse.getId());
        return categoryResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        log.info("Get category with id={}", categoryId);
        return categoryMapper.categoryToResponse(findCategoryById(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<CategoryDto> getCategories(int from, int size) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<Category> page = categoryRepository.findAll(pageable);

        log.info("Get users with {from, size} = ({}, {})", from, size);
        return page.getContent().stream().map(categoryMapper::categoryToResponse).toList();
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryMergeRequest, Long categoryId) {
        Category oldCategory = findCategoryById(categoryId);
        oldCategory.setName(categoryMergeRequest.getName());
        CategoryDto categoryResponse = categoryMapper.categoryToResponse(categoryRepository.save(oldCategory));
        log.info("Category with id={} was updated", categoryId);
        return categoryResponse;
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        if (categoryRepository.deleteCategoriesById(categoryId).isPresent()) {
            log.info("Category with id={} was deleted", categoryId);
        } else {
            throw new NotFoundException(String.format("Category with id=%d not found", categoryId));
        }
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%d not found", categoryId)));
    }
}
