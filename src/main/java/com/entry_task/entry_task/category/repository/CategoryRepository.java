package com.entry_task.entry_task.category.repository;

import com.entry_task.entry_task.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Modifying
    @Query(value = "DELETE FROM product_categories WHERE category_id = :categoryId", nativeQuery = true)
    void deleteCategoryAssociations(@Param("categoryId") Long categoryId);
}
