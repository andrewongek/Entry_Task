package com.entry_task.entry_task.exceptions;

public class InvalidCartItemException extends RuntimeException {
  public InvalidCartItemException(String message) {
    super(message);
  }
}
