package com.entry_task.entry_task.product.mapper;

import com.entry_task.entry_task.product.dto.ProductDetailResponse;
import com.entry_task.entry_task.product.dto.ProductInfo;
import com.entry_task.entry_task.product.dto.ProductListing;
import com.entry_task.entry_task.product.dto.cache.ProductDynamic;
import com.entry_task.entry_task.product.dto.cache.ProductStatic;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.repository.projections.ProductDetailProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "sellerId", source = "seller.id")
  ProductListing toProductListing(Product product);

  @Mapping(target = "sellerId", source = "seller.id")
  @Mapping(target = "status", source = "productStatus")
  ProductInfo toProductInfo(Product product);

  ProductInfo toProductInfo(ProductStatic productStatic, ProductDynamic productDynamic);

  @Mapping(target = "categories", source = "categoryIds")
  @Mapping(target = "cTime", source = "CTime")
  @Mapping(target = "mTime", source = "MTime")
  ProductDetailResponse toProductDetailResponse(ProductDetailProjection projection);
}
