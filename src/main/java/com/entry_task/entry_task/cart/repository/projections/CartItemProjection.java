package com.entry_task.entry_task.cart.repository.projections;

public interface CartItemProjection {

    Long getCartItemId();

    int getQuantity();

    Long getProductId();

    String getProductName();

    int getProductPrice();

    int getProductStock();

    Long getSellerId();

    int getSubTotalPrice();

    Long getCartUpdatedAt();
}
