package com.entry_task.entry_task.category.controller;

import com.entry_task.entry_task.category.dto.CreateCategoryRequest;
import com.entry_task.entry_task.category.dto.DeleteCategoryRequest;
import com.entry_task.entry_task.category.entity.Category;
import com.entry_task.entry_task.category.service.CategoryService;
import com.entry_task.entry_task.common.api.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Category", description = "Admin operations related to category management")
@Validated
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/category")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Operation(
      summary = "Get all categories",
      description = "Retrieves a set of all available categories in the system.")
  @GetMapping()
  public ResponseEntity<CustomApiResponse<Set<Category>>> getCategories() {
    Set<Category> categories = categoryService.getCategories();
    return ResponseEntity.ok().body(CustomApiResponse.success("success", categories));
  }

  @Operation(
      summary = "Create a new category",
      description = "Creates a new category with the provided details.")
  @PostMapping()
  public ResponseEntity<CustomApiResponse<Void>> createCategory(
      @Valid @RequestBody CreateCategoryRequest request) {
    categoryService.createCategory(request);
    return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
  }

  @Operation(
      summary = "Delete a category",
      description = "Deletes an existing category based on the provided identifier.")
  @DeleteMapping()
  public ResponseEntity<CustomApiResponse<Void>> deleteCategory(
      @Valid @RequestBody DeleteCategoryRequest request) {
    categoryService.deleteCategory(request);
    return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
  }
}
