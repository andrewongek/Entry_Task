package com.entry_task.entry_task.product.repository.projections;

import com.entry_task.entry_task.enums.ProductStatus;
import java.util.List;

public interface ProductDetailProjection {
  Long getId();

  String getName();

  Long getSellerId();

  Integer getStock();

  Integer getPrice();

  List<Long> getCategoryIds();

  String getDescription();

  ProductStatus getProductStatus();

  Long getCTime();

  Long getMTime();
}
