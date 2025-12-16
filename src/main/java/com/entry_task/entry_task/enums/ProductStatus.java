package com.entry_task.entry_task.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of a product")
public enum ProductStatus {

    @Schema(description = "Product has been deleted and is no longer available")
    DELETED,

    @Schema(description = "Product is active and available for purchase")
    ACTIVE,

    @Schema(description = "Product is inactive and cannot be purchased or viewed by user")
    INACTIVE
}