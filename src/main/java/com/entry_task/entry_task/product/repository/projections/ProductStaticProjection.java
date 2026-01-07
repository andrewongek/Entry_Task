package com.entry_task.entry_task.product.repository.projections;

import com.entry_task.entry_task.enums.ProductStatus;
import java.util.Set;

public interface ProductStaticProjection {
  Long getId();

  String getName();

  Long getSellerId();

  Set<Long> getCategoryIds();

  String getDescription();

  ProductStatus getProductStatus();
}
