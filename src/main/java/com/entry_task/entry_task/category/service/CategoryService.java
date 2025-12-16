package com.entry_task.entry_task.category.service;

import com.entry_task.entry_task.category.dto.CreateCategoryRequest;
import com.entry_task.entry_task.exceptions.CategoryAlreadyExistsException;
import com.entry_task.entry_task.exceptions.CategoryNotFoundException;
import com.entry_task.entry_task.category.entity.Category;
import com.entry_task.entry_task.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void createCategory(CreateCategoryRequest createCategoryRequest) {
        if (categoryRepository.existsByName(createCategoryRequest.name())) {
            throw new CategoryAlreadyExistsException(
                    "Category with name '" + createCategoryRequest.name() + "' already exists"
            );
        }
        Category category = new Category();
        category.setName(createCategoryRequest.name());

        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        category.getProducts().forEach(product -> product.getCategories().remove(category));
        category.getProducts().clear();

        categoryRepository.delete(category);
    }

    public Set<Category> loadCategories(Set<Long> categoryIds) {
        Set<Category> set = new HashSet<>(categoryRepository.findAllById(categoryIds));
        if (set.size() != categoryIds.size()) {
            throw new CategoryNotFoundException("One or more categories do not exist");
        }
        return set;
    }
}