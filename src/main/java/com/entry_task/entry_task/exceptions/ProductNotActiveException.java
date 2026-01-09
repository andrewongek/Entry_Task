package com.entry_task.entry_task.exceptions;

public class ProductNotActiveException extends RuntimeException {
  public ProductNotActiveException(String message) {
    super(message);
  }

  public ProductNotActiveException() {
    super("Product is not Active");
  }
}
