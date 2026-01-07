package com.entry_task.entry_task.category.service;

import com.entry_task.entry_task.category.dto.CreateCategoryRequest;
import com.entry_task.entry_task.category.dto.DeleteCategoryRequest;
import com.entry_task.entry_task.category.entity.Category;
import com.entry_task.entry_task.category.repository.CategoryRepository;
import com.entry_task.entry_task.exceptions.CategoryAlreadyExistsException;
import com.entry_task.entry_task.exceptions.CategoryNotFoundException;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Transactional
  public void createCategory(CreateCategoryRequest createCategoryRequest) {
    Category category = new Category(createCategoryRequest.name().trim());
    try {
      categoryRepository.save(category);
      log.info("Category created: categoryId={}", category.getId());
    } catch (DataIntegrityViolationException e) {
      throw new CategoryAlreadyExistsException(
          "Category with name '" + createCategoryRequest.name() + "' already exists");
    }
  }

  @Transactional
  public void deleteCategory(DeleteCategoryRequest request) {
    Category category =
        categoryRepository
            .findById(request.id())
            .orElseThrow(() -> new CategoryNotFoundException(request.id()));

    categoryRepository.delete(category);
    log.info("Category deleted: categoryId={}", category.getId());
  }

  public Set<Category> loadCategories(Set<Long> categoryIds) {
    Set<Category> set = new HashSet<>(categoryRepository.findAllById(categoryIds));
    if (set.size() != categoryIds.size()) {
      throw new CategoryNotFoundException("One or more categories do not exist");
    }
    return set;
  }
}
