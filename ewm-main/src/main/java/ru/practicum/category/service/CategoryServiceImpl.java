package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.repository.MemoryCategory;
import ru.practicum.category.dto.CategoryDtoRequest;
import ru.practicum.category.dto.CategoryDtoResponse;
import ru.practicum.event.repository.MemoryEvent;
import ru.practicum.exception.exceptions.AccessDeniedException;
import ru.practicum.exception.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final MemoryCategory memoryCategory;
    private final MemoryEvent memoryEvent;

    @Override
    public CategoryDtoResponse createCategoryByAdmin(CategoryDtoRequest request) {
        Category category = memoryCategory.save(CategoryMapper.toCategory(request));
        log.info("New category with ID {} was ben created", category.getId());

        return CategoryMapper.toCategoryDtoResponse(category);
    }

    @Override
    public void deleteCategoryByAdmin(Long id) {
        if (!isCategoryExists(id)) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
        if (memoryEvent.existsByCategoryId(id)) {
            throw new AccessDeniedException("The category is not empty");
        } else {
            memoryCategory.deleteById(id);
            log.info(("Category with id={} was deleted"), id);
        }
    }

    @Override
    public CategoryDtoResponse updateCategoryByAdmin(Long id, CategoryDtoRequest request) {
        Category category = memoryCategory.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));

        if (!category.getName().equals(request.getName()) && memoryCategory.existsByNameIgnoreCase(request.getName())) {
            throw new AccessDeniedException("Category name duplication for: " + request.getName());
        }

        category.setName(request.getName());
        Category updated = memoryCategory.save(category);
        log.info("Category with id={} was not updated", updated.getId());

        return CategoryMapper.toCategoryDtoResponse(updated);
    }

    @Override
    public List<CategoryDtoResponse> getCategoriesPublic(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Category> categories = memoryCategory.findAll(pageable);

        log.info("{} categories found", categories.getSize());

        return categories.stream()
                .map(CategoryMapper::toCategoryDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDtoResponse getCategoryPublic(Long catId) {
        Category category = memoryCategory.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        log.info("Category {} was found", category.getId());

        return CategoryMapper.toCategoryDtoResponse(category);
    }

    private Boolean isCategoryExists(Long id) {
        return memoryCategory.existsById(id);
    }
}