package com.entry_task.entry_task.category.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.entry_task.entry_task.category.dto.CreateCategoryRequest;
import com.entry_task.entry_task.category.dto.DeleteCategoryRequest;
import com.entry_task.entry_task.category.entity.Category;
import com.entry_task.entry_task.category.repository.CategoryRepository;
import com.entry_task.entry_task.common.TestEntityFactory;
import com.entry_task.entry_task.exceptions.CategoryAlreadyExistsException;
import com.entry_task.entry_task.exceptions.CategoryNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CategoryServiceTest {

  @InjectMocks private CategoryService categoryService;

  @Mock private CategoryRepository categoryRepository;

  @Captor ArgumentCaptor<Category> categoryCaptor;

  @Test
  void createCategory_uniqueCategoryName_shouldSaveCategory() {
    // Given
    Category category = TestEntityFactory.createCategory("test");

    // When
    categoryService.createCategory(new CreateCategoryRequest(category.getName()));

    // Then
    verify(categoryRepository).save(categoryCaptor.capture());
    Category savedCategory = categoryCaptor.getValue();

    assertEquals(category.getName(), savedCategory.getName());
  }

  @Test
  void createCategory_duplicatedCategoryName_shouldThrowCategoryAlreadyExistsException() {
    // Given
    Category category = TestEntityFactory.createCategory("test");

    // When
    when(categoryRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));

    // Then
    CategoryAlreadyExistsException ex =
        assertThrows(
            CategoryAlreadyExistsException.class,
            () -> categoryService.createCategory(new CreateCategoryRequest(category.getName())));
    assertEquals("Category with name '" + category.getName() + "' already exists", ex.getMessage());
  }

  @Test
  void deleteCategory_categoryExists_shouldDeleteCategory() {
    // Given
    Category category = TestEntityFactory.createCategory("test");
    category.setId(1L);

    // When
    when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
    categoryService.deleteCategory(new DeleteCategoryRequest(category.getId()));

    // Then
    verify(categoryRepository).delete(categoryCaptor.capture());
    Category deletedCategory = categoryCaptor.getValue();
    assertEquals(category.getName(), deletedCategory.getName());
  }

  @Test
  void deleteCategory_categoryDoesNotExists_shouldThrowCategoryNotFoundException() {
    // Given
    Long categoryId = 1L;

    // When
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    // Then
    CategoryNotFoundException ex =
        assertThrows(
            CategoryNotFoundException.class,
            () -> categoryService.deleteCategory(new DeleteCategoryRequest(categoryId)));
    assertEquals("Category not found with id: " + categoryId, ex.getMessage());
  }

  @Test
  void loadCategories_allCategoriesIdsValid_shouldReturnCategorySet() {
    // Given
    Category category1 = TestEntityFactory.createCategory("category1");
    category1.setId(1L);
    Category category2 = TestEntityFactory.createCategory("category2");
    category2.setId(2L);

    List<Long> idList = List.of(category1.getId(), category2.getId());
    Set<Long> idSet = Set.copyOf(idList);
    // When
    when(categoryRepository.findAllById(any())).thenReturn(List.of(category1, category2));
    Set<Category> result = categoryService.loadCategories(idSet);

    assertNotNull(result, "Resulting category set should not be null");

    assertEquals(
        idSet.size(), result.size(), "Resulting category set size should match input ID set");

    Set<Long> resultIds = result.stream().map(Category::getId).collect(Collectors.toSet());

    assertEquals(idSet, resultIds, "Returned category IDs should match the input ID set");
  }

  @Test
  void loadCategories_oneCategoryIdNotFound_shouldThrowCategoryNotFoundException() {
    // Given
    Category category1 = TestEntityFactory.createCategory("category1");
    category1.setId(1L);
    Long category2Id = 2L;

    List<Long> idList = List.of(category1.getId(), category2Id);
    Set<Long> idSet = Set.copyOf(idList);
    // When
    when(categoryRepository.findAllById(any())).thenReturn(List.of(category1));

    // Then
    CategoryNotFoundException ex =
        assertThrows(CategoryNotFoundException.class, () -> categoryService.loadCategories(idSet));
    assertEquals("One or more categories do not exist", ex.getMessage());
  }
}
