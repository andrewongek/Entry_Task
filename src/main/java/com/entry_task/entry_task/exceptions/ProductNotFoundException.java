package com.entry_task.entry_task.exceptions;

public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(String message) {
    super(message);
  }

  public ProductNotFoundException() {
    super("Product not found");
  }
}
