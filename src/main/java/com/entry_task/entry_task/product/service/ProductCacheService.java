package com.entry_task.entry_task.product.service;

import com.entry_task.entry_task.exceptions.ProductNotFoundException;
import com.entry_task.entry_task.product.dto.cache.ProductDynamic;
import com.entry_task.entry_task.product.dto.cache.ProductStatic;
import com.entry_task.entry_task.product.repository.ProductRepository;
import com.entry_task.entry_task.product.repository.projections.ProductDynamicProjection;
import com.entry_task.entry_task.product.repository.projections.ProductStaticProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductCacheService {
  private final ProductRepository productRepository;
  private static final Logger log = LoggerFactory.getLogger(ProductCacheService.class);

  public ProductCacheService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Cacheable(
      value = "product:static",
      key = "#productId",
      sync = true) // sync=true to ensure only one thread loads the data on cache miss
  public ProductStatic getProductStatic(long productId) {
    log.debug("Cache MISS: product:static [{}]", productId);
    ProductStaticProjection p =
        productRepository.findProductStatic(productId).orElseThrow(ProductNotFoundException::new);
    return new ProductStatic(
        p.getId(),
        p.getName(),
        p.getSellerId(),
        p.getCategoryIds(),
        p.getDescription(),
        p.getProductStatus());
  }

  @Cacheable(value = "product:dynamic", key = "#productId", sync = true)
  public ProductDynamic getProductDynamic(long productId) {
    ProductDynamicProjection p =
        productRepository.findProductDynamic(productId).orElseThrow(ProductNotFoundException::new);
    return new ProductDynamic(p.getStock(), p.getPrice());
  }
}
