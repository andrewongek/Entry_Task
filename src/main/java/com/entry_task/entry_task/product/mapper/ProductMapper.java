package com.entry_task.entry_task.product.mapper;

import com.entry_task.entry_task.product.dto.ProductInfo;
import com.entry_task.entry_task.product.dto.ProductListing;
import com.entry_task.entry_task.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "sellerId", source = "seller.id")
  ProductListing productToProductListing(Product product);

  @Mapping(target = "sellerId", source = "seller.id")
  @Mapping(target = "status", source = "productStatus")
  ProductInfo productToProductInfo(Product product);
}
