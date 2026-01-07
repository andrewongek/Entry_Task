package com.entry_task.entry_task.exceptions;

public class ProductAlreadyFavouritedException extends RuntimeException {
  public ProductAlreadyFavouritedException() {
    super("Product is already in User's Favourites");
  }
}
