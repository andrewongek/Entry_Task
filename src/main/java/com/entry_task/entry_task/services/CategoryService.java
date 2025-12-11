package com.entry_task.entry_task.services;

import com.entry_task.entry_task.dto.CreateCategoryRequest;
import com.entry_task.entry_task.model.Category;
import com.entry_task.entry_task.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void createCategory(CreateCategoryRequest createCategoryRequest) {
        if (categoryRepository.existsByName(createCategoryRequest.name())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }
        Category category = new Category();
        category.setName(createCategoryRequest.name());

        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Break ManyToMany relationship safely
        category.getProducts().forEach(product -> product.getCategories().remove(category));
        category.getProducts().clear();

        categoryRepository.delete(category);
    }
}