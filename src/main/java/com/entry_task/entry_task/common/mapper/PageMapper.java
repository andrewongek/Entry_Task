package com.entry_task.entry_task.common.mapper;

import com.entry_task.entry_task.common.dto.Metadata;
import com.entry_task.entry_task.product.dto.ProductListResponse;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface PageMapper {

  default <T> ProductListResponse<T> toProductListResponse(Page<T> page) {
    return new ProductListResponse<>(
        page.getContent(),
        new Metadata(page.getTotalElements(), page.getNumber(), page.getSize(), page.hasNext()));
  }
}
