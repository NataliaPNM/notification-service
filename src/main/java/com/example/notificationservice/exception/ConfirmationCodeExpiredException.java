package com.example.notificationservice.exception;

public class ConfirmationCodeExpiredException extends RuntimeException {

  public ConfirmationCodeExpiredException(String message) {
    super(message);
  }
}
