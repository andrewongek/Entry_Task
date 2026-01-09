package com.entry_task.entry_task.exceptions;

public class InvalidJwtException extends RuntimeException {

  public InvalidJwtException(String message) {
    super(message);
  }

  public InvalidJwtException(String message, Throwable cause) {
    super(message, cause);
  }
}
